package com.example.resourceallocation.discovery;

import com.example.resourceallocation.discovery.persistence.DiscoveryRegistrationRepository;
import com.example.resourceallocation.discovery.persistence.RegistryPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DiscoveryServerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        r.add("spring.datasource.username", () -> postgres.getUsername());
        r.add("spring.datasource.password", () -> postgres.getPassword());
        r.add("spring.jpa.hibernate.ddl-auto", () -> "update");
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
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
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
