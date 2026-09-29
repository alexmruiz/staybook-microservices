package com.staybook.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@TestConfiguration
public class TestBeansConfig {

    // Only define a test AuthenticationManager if the application context does not
    // already provide one. This prevents BeanDefinitionOverrideException when the
    // real SecurityConfig exposes an `authenticationManager` bean.
    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager testAuthenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                authentication.setAuthenticated(true);
                return authentication;
            }
        };
    }
}
