package com.staybook.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient 
class JwtAuthenticationFilterTest {

    @Autowired 
    private WebTestClient webClient;

    @Test 
    void request_WithoutToken_ShouldReturn401() {
        webClient.get().uri("/api/hotels")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void request_ToPublicPath_ShouldNotRequireToken() {
        webClient.post().uri("/api/auth/login")
                .exchange()
                .expectStatus().isEqualTo(500);// puede dar 400/503 según el entorno, pero nunca 401
    }
}
