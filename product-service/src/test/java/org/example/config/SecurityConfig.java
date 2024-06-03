package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> jwt();
    }

    @Bean
    public Jwt jwt() {
        return Jwt.withTokenValue("mock-token")
                        .header("alg", "none")
                        .claim("iat", Instant.now())
                        .claim("exp", Instant.now().plusSeconds(3600)) // 1 hour
                        .build();
    }
}