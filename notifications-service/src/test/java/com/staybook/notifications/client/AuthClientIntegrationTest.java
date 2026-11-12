package com.staybook.notifications.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.staybook.notifications.dto.UserSummaryResponseDto;

@SpringBootTest
class AuthClientIntegrationTest {

    private static final String API_KEY = "integration-test-api-key";
    private static final AtomicReference<String> requestPath = new AtomicReference<>();
    private static final AtomicReference<String> requestApiKey = new AtomicReference<>();
    private static HttpServer authServer;

    @Autowired
    private AuthClient authClient;

    @DynamicPropertySource
    static void configureAuthService(DynamicPropertyRegistry registry) {
        try {
            authServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            authServer.createContext("/internal/users/42", exchange -> {
                requestPath.set(exchange.getRequestURI().getPath());
                requestApiKey.set(exchange.getRequestHeaders().getFirst("X-Internal-Api-Key"));
                byte[] response = """
                        {"id":42,"email":"ada@example.com","name":"Ada","surname":"Lovelace"}
                        """.strip().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream body = exchange.getResponseBody()) {
                    body.write(response);
                }
            });
            authServer.start();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo iniciar el servidor HTTP de prueba", exception);
        }

        registry.add("auth.service.url", () -> "http://localhost:" + authServer.getAddress().getPort());
        registry.add("internal.api-key", () -> API_KEY);
    }

    @AfterAll
    static void stopAuthServer() {
        if (authServer != null) {
            authServer.stop(0);
        }
    }

    @Test
    void getUserById_shouldCallInternalEndpointWithApiKeyAndDeserializeResponse() {
        UserSummaryResponseDto user = authClient.getUserById(42L);

        assertEquals("/internal/users/42", requestPath.get());
        assertEquals(API_KEY, requestApiKey.get());
        assertEquals(new UserSummaryResponseDto(42L, "ada@example.com", "Ada", "Lovelace"), user);
    }
}
