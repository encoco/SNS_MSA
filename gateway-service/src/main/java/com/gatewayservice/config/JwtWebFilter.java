package com.gatewayservice.config;

import com.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtWebFilter implements WebFilter {

    private final TokenProvider tokenProvider;

    private static final String[] WHITELIST = {
            "/api/users/Login", "/api/users/join", "/api/auth/refresh",
            "/api/users/Logout", "/api/chats/ws"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // preflight는 CorsWebFilter가 처리
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();
        for (String white : WHITELIST) {
            if (path.startsWith(white)) {
                return chain.filter(exchange);
            }
        }

        String token = extractToken(exchange.getRequest());
        if (token != null && tokenProvider.validateToken(token)) {
            int userId = tokenProvider.getUserIdFromToken(token);
            String nickname = tokenProvider.getNicknameFromToken(token);

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Nickname", nickname)
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
