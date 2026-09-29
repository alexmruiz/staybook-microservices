package com.staybook.auth.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staybook.auth.controller.AuthControllerTest.TestConfig;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.service.AuthService;

@WebMvcTest(AuthController.class)
@Import(TestConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService service;

    private RegisterRequestDto registerRequestDto;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        TypeRole role = TypeRole.ROLE_ADMIN;
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 5, 6);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 12, 8, 5, 5);
        registerRequestDto = new RegisterRequestDto("email@email.com", "password", "name", "surname");
        responseDto = new UserResponseDto(1L, "email@email.com", "name", "surname", role, createdAt, updatedAt);
    }

    @Nested
    @DisplayName("Test para function create()")
    class registerTest {

        @Test
        @WithMockUser(roles = "ADMIN")
        void register_ShouldReturnRegisterRequestDto() throws Exception {
            when(service.register(registerRequestDto)).thenReturn(responseDto);

            mockMvc.perform(post("/api/auth/register").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("name"))
                    .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return mock(AuthService.class);
        }
    }
}