package com.example.resourceallocation.discovery;

import com.example.resourceallocation.discovery.persistence.DiscoveryRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "spring.main.lazy-initialization=true"
})
public class DiscoveryServerSmokeTest {

    @Autowired
    private DiscoveryRegistrationRepository repository;

    @Test
    public void contextLoadsAndRepositoryPresent() {
        assertThat(repository).isNotNull();
    }
}
