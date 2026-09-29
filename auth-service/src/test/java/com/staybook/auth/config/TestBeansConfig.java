package com.staybook.auth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@TestConfiguration
public class TestBeansConfig {

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                authentication.setAuthenticated(true);
                return authentication;
            }
        };
    }
}
