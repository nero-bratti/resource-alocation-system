package com.example.resourceallocation.discovery.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "discovery_registration", uniqueConstraints = {@UniqueConstraint(columnNames = {"app_name", "instance_id"})})
public class DiscoveryRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", nullable = false)
    private String appName;

    @Column(name = "instance_id", nullable = false)
    private String instanceId;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "port")
    private Integer port;

    @Column(name = "secure_port")
    private Integer securePort;

    @Lob
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "status")
    private String status;

    @Column(name = "last_renewal_ts")
    private Long lastRenewalTimestamp;

    @Column(name = "lease_duration")
    private Integer leaseDurationSecs;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public DiscoveryRegistration() {
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public String getIpAddr() { return ipAddr; }
    public void setIpAddr(String ipAddr) { this.ipAddr = ipAddr; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public Integer getSecurePort() { return securePort; }
    public void setSecurePort(Integer securePort) { this.securePort = securePort; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getLastRenewalTimestamp() { return lastRenewalTimestamp; }
    public void setLastRenewalTimestamp(Long lastRenewalTimestamp) { this.lastRenewalTimestamp = lastRenewalTimestamp; }
    public Integer getLeaseDurationSecs() { return leaseDurationSecs; }
    public void setLeaseDurationSecs(Integer leaseDurationSecs) { this.leaseDurationSecs = leaseDurationSecs; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
