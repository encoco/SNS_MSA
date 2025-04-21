package com.userservice.config;

import com.common.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationTime.access}")
    private long access;

    @Value("${jwt.expirationTime.refresh}")
    private long refresh;

    @Bean
    public TokenProvider tokenProvider() {
        return new TokenProvider(secret, access, refresh);
    }
}