package com.staybook.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.staybook.auth.dto.request.LoginRequestDto;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.exception.EmailAlreadyExistsException;
import com.staybook.auth.exception.InvalidCredentialsException;
import com.staybook.auth.mapper.AuthMapper;
import com.staybook.auth.repository.AuthRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository repository;

    @Mock
    private AuthMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private com.staybook.auth.security.JwtService jwtService;

    @InjectMocks
    private AuthService service;

    private Auth auth;
    private UserResponseDto response;
    private RegisterRequestDto requestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 5, 5, 5);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 10, 6, 6, 6);

        this.auth = new Auth("email@email.com", "name", "surname");
        this.response = new UserResponseDto(1L, "email@email.com", "name", "surname", TypeRole.ROLE_USER, createdAt,
                updatedAt);
        this.requestDto = new RegisterRequestDto("email@email.com", "testadmin", "name", "surname");
    }

    @Nested
    @DisplayName("Test funcion resgister()")
    class resgisterTest {

        @Test
        void register_WhenEmailDoesNotExist_ShouldRegisterUserSuccessfully() {

            // When
            when(repository.findByEmail(requestDto.email())).thenReturn(Optional.empty());
            when(mapper.toEntity(requestDto)).thenReturn(auth);
            when(passwordEncoder.encode(requestDto.password())).thenReturn("hashedPassword");
            when(repository.save(auth)).thenReturn(auth);
            when(mapper.toResponseDto(auth)).thenReturn(response);

            UserResponseDto result = service.register(requestDto);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("name", result.name());
            assertEquals("surname", result.surname());

            // Verificamos que se encriptó la contraseña y se guardó en BD
            verify(passwordEncoder).encode(requestDto.password());
            verify(repository).save(auth);

        }

        @Test
        void register_WhenEmailAlreadyExists_ShouldThrowException() {

            // Simulamos que el repositorio encuentra un usuario con ese correo
            when(repository.findByEmail(requestDto.email())).thenReturn(Optional.of(new Auth()));

            // Act & Assert (Ejecución y Verificación de la excepción)
            assertThrows(EmailAlreadyExistsException.class, () -> service.register(requestDto));

            // Verificamos que NUNCA se intentó guardar ni encriptar contraseña si el email
            // ya existía
            verify(repository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    @DisplayName("Test login() errors")
    class loginTest {

        @Test
        void login_WithWrongPassword_ShouldReturn401() throws Exception {
            // Arrange
            LoginRequestDto login = new LoginRequestDto("email@email.com", "wrongpass");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            assertThrows(InvalidCredentialsException.class, () -> service.login(login));
        }

        @Test
        void login_WithNonExistentEmail_ShouldReturn401() throws Exception {
            // Arrange
            LoginRequestDto login = new LoginRequestDto("noone@nowhere.com", "whatever");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            assertThrows(InvalidCredentialsException.class, () -> service.login(login));
        }
    }

}
