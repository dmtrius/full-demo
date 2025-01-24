package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

//@Configuration
public class JwtConfig {
//    @Value("${keycloak.jwks-url}")
//    private String jwksUrl;
//
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withJwkSetUri(jwksUrl).build();
//    }
}
