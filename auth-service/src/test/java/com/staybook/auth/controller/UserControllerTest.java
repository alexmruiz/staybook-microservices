package com.staybook.auth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.service.AuthService;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService service;

    private Auth auth;
    private UserResponseDto response;
    private UserResponseDto responseUpdate;
    private RegisterRequestDto requestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 5, 5, 5);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 10, 6, 6, 6);

        this.auth = new Auth("email@email.com", "name", "surname");
        this.response = new UserResponseDto(1L, "email@email.com", "name", "surname", TypeRole.ROLE_USER, createdAt,
                updatedAt);
        responseUpdate = new UserResponseDto(1L, "email@email.test", "name1", "surname1", TypeRole.ROLE_USER, createdAt,
                updatedAt);
        requestDto = new RegisterRequestDto("email@email.test", "passwordTest", "name1", "surname1");
    }

    @Nested
    @DisplayName("Test funciton getMyProfile()")
    class GetMyProfile {

        @Test
        void getMyProfile_shouldReturnUserResponseDto() throws Exception {

            when(service.getEmailUser(auth.getEmail())).thenReturn(response);
            mockMvc.perform(get("/api/users/me")
                    .with(user(auth.getEmail())) // Simula que la petición viene autenticada con este email
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value(auth.getEmail()))
                    .andExpect(jsonPath("$.name").value("name"))
                    .andExpect(jsonPath("$.role").value("ROLE_USER"));
        }

        @Test
        @DisplayName("GET /api/users/me - Error: Retorna 401 Unauthorized si no hay usuario autenticado")
        void getMyProfile_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
            // Act & Assert: Sin el .with(user(...)), Spring Security detectará que no hay
            // sesión o token válido
            mockMvc.perform(get("/api/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Test function updateUserProfile()")
    class UpdateUserProfileTest {

        @Test
        void updateUserProfile_ShouldReturnUserResponseDto() throws Exception {
            when(service.updateUserProfile(auth.getEmail(), requestDto)).thenReturn(responseUpdate);

            mockMvc.perform(put("/api/users/me")
                    .with(user(auth.getEmail()))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("email@email.test"))
                    .andExpect(jsonPath("$.name").value("name1"))
                    .andExpect(jsonPath("$.surname").value("surname1"));
        }

        @Test
        @DisplayName("PUT /api/users/me - Error: Retorna 401 Unauthorized si no hay usuario autenticado")
        void updateMyProfile_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put("/api/users/me")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isUnauthorized());
        }
    }

}
