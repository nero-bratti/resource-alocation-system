package com.example.resourceallocation.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@WebFluxTest
@Import(GatewaySecurityConfigTest.TestConfig.class)
class GatewaySecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void apiRoutesRequireAuthentication() {
        webTestClient.get()
                .uri("/api/databank")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void authenticatedRequestsAreAccepted() {
        webTestClient.get()
                .uri("/api/databank")
                .headers(headers -> headers.setBearerAuth("test-token"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void healthEndpointRemainsPublic() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        RouterFunction<ServerResponse> testRoutes() {
            return route(GET("/api/databank"), request -> ServerResponse.ok().bodyValue("ok"));
        }
    }
}
