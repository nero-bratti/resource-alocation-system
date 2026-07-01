package com.example.resourceallocation.discovery.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Component
public class RegistryPersistenceService {
    private static final Logger log = LoggerFactory.getLogger(RegistryPersistenceService.class);
    private final DiscoveryRegistrationRepository repo;
    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RegistryPersistenceService(DiscoveryRegistrationRepository repo, RestTemplate rest) {
        this.repo = repo;
        this.rest = rest;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadOnStartup() {
        log.info("Discovery registry loader starting - restoring persisted instances");
        repo.findAll().forEach(r -> {
            try {
                JsonNode instance = buildInstanceNodeFromRegistration(r);
                String url = String.format("http://localhost:8761/eureka/apps/%s", r.getAppName());
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.add("Content-Type", "application/json");
                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(mapper.writeValueAsString(instance), headers);
                rest.postForLocation(Objects.requireNonNull(url), entity);
                log.info("Restored instance {} for app {}", r.getInstanceId(), r.getAppName());
            } catch (Exception e) {
                log.warn("Failed to restore instance {}: {}", r.getInstanceId(), e.getMessage());
            }
        });
    }

    @Scheduled(initialDelayString = "15000", fixedDelayString = "30000")
    @Transactional
    public void snapshotRegistry() {
        try {
            String raw = rest.getForObject("http://localhost:8761/eureka/apps?format=json", String.class);
            JsonNode root = mapper.readTree(raw);
            JsonNode applications = root.path("applications");
            if (applications.isMissingNode()) return;
            JsonNode application = applications.path("application");
            if (application.isMissingNode()) return;

            if (application.isArray()) {
                for (JsonNode appNode : application) {
                    processAppNode(appNode);
                }
            } else {
                processAppNode(application);
            }
        } catch (Exception e) {
            log.debug("Could not snapshot registry: {}", e.getMessage());
        }
    }

    private void processAppNode(JsonNode appNode) {
        String appName = appNode.path("name").asText();
        JsonNode instances = appNode.path("instance");
        if (instances.isMissingNode()) return;
        if (instances.isArray()) {
            for (JsonNode inst : instances) upsertInstance(appName, inst);
        } else {
            upsertInstance(appName, instances);
        }
    }

    private void upsertInstance(String appName, JsonNode inst) {
        try {
            String instanceId = inst.path("instanceId").asText(inst.path("hostName").asText());
            Optional<DiscoveryRegistration> existing = repo.findByAppNameAndInstanceId(appName, instanceId);
            DiscoveryRegistration reg = existing.orElseGet(DiscoveryRegistration::new);
            reg.setAppName(appName);
            reg.setInstanceId(instanceId);
            reg.setHostName(inst.path("hostName").asText(null));
            reg.setIpAddr(inst.path("ipAddr").asText(null));
            reg.setPort(inst.path("port").path("$" ).asInt(inst.path("port").asInt(0)));
            reg.setSecurePort(inst.path("securePort").path("$" ).asInt(inst.path("securePort").asInt(0)));
            reg.setStatus(inst.path("status").asText(null));
            reg.setMetadata(inst.path("metadata").toString());
            reg.setLastRenewalTimestamp(inst.path("leaseInfo").path("lastRenewalTimestamp").asLong(0L));
            reg.setLeaseDurationSecs(inst.path("leaseInfo").path("durationInSecs").asInt(0));
            repo.save(reg);
        } catch (Exception e) {
            log.debug("Failed to upsert instance for app {}: {}", appName, e.getMessage());
        }
    }

    private JsonNode buildInstanceNodeFromRegistration(DiscoveryRegistration r) {
        // build a minimal wrapper matching Eureka's expected payload: { "instance": { ... } }
        ObjectMapper m = mapper;
        JsonNode root = m.createObjectNode();
        JsonNode instance = m.createObjectNode();
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).put("instanceId", r.getInstanceId());
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).put("hostName", r.getHostName());
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).put("app", r.getAppName());
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).put("ipAddr", r.getIpAddr());
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).put("status", r.getStatus() == null ? "UP" : r.getStatus());
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).set("port", m.createObjectNode().put("$", r.getPort() == null ? 0 : r.getPort()).put("@enabled", r.getPort() != null));
        ((com.fasterxml.jackson.databind.node.ObjectNode) instance).set("securePort", m.createObjectNode().put("$", r.getSecurePort() == null ? 0 : r.getSecurePort()).put("@enabled", r.getSecurePort() != null));
        if (r.getMetadata() != null) {
            try {
                JsonNode md = m.readTree(r.getMetadata());
                ((com.fasterxml.jackson.databind.node.ObjectNode) instance).set("metadata", md);
            } catch (Exception e) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) instance).put("metadata", r.getMetadata());
            }
        }
        ((com.fasterxml.jackson.databind.node.ObjectNode) root).set("instance", instance);
        return root;
    }
}
