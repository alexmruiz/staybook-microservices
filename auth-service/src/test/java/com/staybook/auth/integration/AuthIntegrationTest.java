package com.staybook.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    private RegisterRequestDto request;

    @BeforeEach
    void setUp() {
        authRepository.deleteAll(); 
        request = new RegisterRequestDto(
            "carlos@example.com", "password123", "name", "surname"
        );
    }

    @Test
    void register_WhenValidRequest_ShouldReturn201Created() throws Exception {
        

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("carlos@example.com"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldReturn409Conflict() throws Exception {

        // 1. Registramos por primera vez (éxito)
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 2. Intentamos registrar de nuevo con el mismo email (debe fallar con 409)
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}