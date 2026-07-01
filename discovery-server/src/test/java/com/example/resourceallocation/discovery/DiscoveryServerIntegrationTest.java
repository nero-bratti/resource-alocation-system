package com.example.resourceallocation.discovery;

import com.example.resourceallocation.discovery.persistence.DiscoveryRegistrationRepository;
import com.example.resourceallocation.discovery.persistence.RegistryPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
public class DiscoveryServerIntegrationTest {

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        r.add("spring.datasource.username", () -> "sa");
        r.add("spring.datasource.password", () -> "");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    DiscoveryRegistrationRepository repository;

    @Autowired
    RegistryPersistenceService persistenceService;

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void snapshotParsesAndSaves() throws Exception {
        // Prepare mock server for RestTemplate to respond with a minimal Eureka apps JSON
        MockRestServiceServer server = MockRestServiceServer.createServer(Objects.requireNonNull(restTemplate));
        String appsJson = "{\"applications\":{\"application\":{\"name\":\"TESTAPP\",\"instance\":{\"instanceId\":\"i1\",\"hostName\":\"h1\",\"ipAddr\":\"10.0.0.1\",\"port\":{\"$\":8080},\"status\":\"UP\"}}}}";
        server.expect(MockRestRequestMatchers.requestTo("http://localhost:8761/eureka/apps?format=json"))
                .andRespond(MockRestResponseCreators.withSuccess(appsJson, MediaType.APPLICATION_JSON));

        // Run snapshot
        persistenceService.snapshotRegistry();

        // Verify persisted
        assertThat(repository.findByAppNameAndInstanceId("TESTAPP", "i1")).isPresent();

        server.verify();
    }
}
