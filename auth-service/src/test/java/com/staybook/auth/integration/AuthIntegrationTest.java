package com.staybook.auth.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staybook.auth.dto.request.LoginRequestDto;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.repository.AuthRepository;
import com.staybook.auth.repository.VerificationTokenRepository;

@SpringBootTest
@Import(com.staybook.auth.config.TestBeansConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequestDto request;

    @BeforeEach
    void setUp() {
        verificationTokenRepository.deleteAll();
        authRepository.deleteAll();
        request = new RegisterRequestDto("carlos@example.com", "password123", "name", "surname");
    }

    @Test
    void register_WhenValidRequest_ShouldReturn201Created() throws Exception {

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.email").value("carlos@example.com"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldReturn409Conflict() throws Exception {

        // 1. Registramos por primera vez (éxito)
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());

        // 2. Intentamos registrar de nuevo con el mismo email (debe fallar con 409)
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
    }

    private void saveUser(boolean emailVerified) {
        Auth user = new Auth();
        user.setEmail("carlos@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setName("Carlos");
        user.setSurname("Ruiz");
        user.setRole(TypeRole.ROLE_USER);
        user.setEmailVerified(emailVerified);

        authRepository.saveAndFlush(user);
    }

    @Test
    void login_WithUnverifiedAccount_ShouldReturn401WithoutToken() throws Exception {
        saveUser(false);

        LoginRequestDto login = new LoginRequestDto("carlos@example.com", "password123");

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void login_WithVerifiedAccount_ShouldReturnJwt() throws Exception {
        saveUser(true);

        LoginRequestDto login = new LoginRequestDto("carlos@example.com", "password123");

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}