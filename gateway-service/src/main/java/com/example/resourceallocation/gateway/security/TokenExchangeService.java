package com.example.resourceallocation.gateway.security;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TokenExchangeService {
    private final WebClient webClient;

    public TokenExchangeService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://keycloak:8080/realms/resource-allocation/protocol/openid-connect/token").build();
    }

    public Mono<String> exchange(String subjectToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        form.add("client_id", "gateway-client");
        form.add("client_secret", "gateway-secret");
        form.add("subject_token", subjectToken);

        return webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken);
    }

    static class TokenResponse {
        private String access_token;

        public String getAccessToken() { return access_token; }
        public void setAccessToken(String access_token) { this.access_token = access_token; }
    }
}
