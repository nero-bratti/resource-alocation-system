package com.example.resourceallocation.gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class TokenExchangeFilter implements WebFilter {
    private final TokenExchangeService exchangeService;

    public TokenExchangeFilter(TokenExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        String subjectToken = auth.substring(7);
        return exchangeService.exchange(subjectToken)
                .flatMap(internalToken -> {
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + internalToken)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                });
    }
}
