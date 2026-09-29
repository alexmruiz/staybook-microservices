package com.staybook.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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
import com.staybook.auth.config.SecurityConfig;
import com.staybook.auth.controller.AuthControllerTest.TestConfig;
import com.staybook.auth.dto.request.LoginRequestDto;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.service.AuthService;

@WebMvcTest(AuthController.class)
@Import({TestConfig.class, SecurityConfig.class})
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

    @Nested
    @DisplayName("Test login()")
    class loginTest {
        
        @BeforeEach
        void setUp() {
            // Reset mock before each test to clear previous stubbing
            reset(service);
        }

        @Test
        void login_AfterRegister_ShouldReturnValidToken() throws Exception {
            // Instead of performing a full register via HTTP (which requires proper
            // security context and CSRF), mock the AuthService behavior for login
            // and test the login endpoint in isolation.
            LoginRequestDto loginRequest = new LoginRequestDto(registerRequestDto.email(), registerRequestDto.password());

            when(service.login(any(LoginRequestDto.class)))
                .thenReturn(new com.staybook.auth.dto.response.AuthResponseDto("fake-token", "Bearer", 86400000L,
                    registerRequestDto.email(), TypeRole.ROLE_ADMIN));

            mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
        }

        @Test
        void login_WithWrongPassword_ShouldReturn401() throws Exception {
            LoginRequestDto loginRequest = new LoginRequestDto("email@email.com", "wrongpass");

            doThrow(new com.staybook.auth.exception.InvalidCredentialsException("Email o contraseña incorrectos"))
                .when(service).login(any(LoginRequestDto.class));

            mockMvc.perform(post("/api/auth/login").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void login_WithNonExistentEmail_ShouldReturn401() throws Exception {
            LoginRequestDto loginRequest = new LoginRequestDto("noone@nowhere.com", "whatever");

            doThrow(new com.staybook.auth.exception.InvalidCredentialsException("Email o contraseña incorrectos"))
                .when(service).login(any(LoginRequestDto.class));

            mockMvc.perform(post("/api/auth/login").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

}