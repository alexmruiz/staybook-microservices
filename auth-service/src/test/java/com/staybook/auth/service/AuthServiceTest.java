package com.staybook.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.staybook.auth.dto.request.LoginRequestDto;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.request.VerifyEmailRequest;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.entity.VerificationToken;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.exception.EmailAlreadyExistsException;
import com.staybook.auth.exception.ExpiredTokenException;
import com.staybook.auth.exception.InvalidCredentialsException;
import com.staybook.auth.exception.InvalidTokenException;
import com.staybook.auth.mapper.AuthMapper;
import com.staybook.auth.repository.AuthRepository;
import com.staybook.auth.repository.VerificationTokenRepository;

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

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

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
            assertFalse(auth.isEnabled());
            assertFalse(auth.isEmailVerified());

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

            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            assertThrows(InvalidCredentialsException.class, () -> service.login(login));
        }

        @Test
        void login_WithNonExistentEmail_ShouldReturn401() throws Exception {
            // Arrange
            LoginRequestDto login = new LoginRequestDto("noone@nowhere.com", "whatever");

            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            assertThrows(InvalidCredentialsException.class, () -> service.login(login));
        }

        @Test
        void isEnabled_WhenEmailIsNotVerified_ShouldReturnFalse() {
            Auth user = new Auth("email@email.com", "name", "surname");

            assertFalse(user.isEmailVerified());
            assertFalse(user.isEnabled());
        }

        @Test
        void isEnabled_WhenEmailIsVerified_ShouldReturnTrue() {
            Auth user = new Auth("email@email.com", "name", "surname");
            user.setEmailVerified(true);

            assertTrue(user.isEmailVerified());
            assertTrue(user.isEnabled());
        }

        @Test
        void login_WithUnverifiedAccount_ShouldNotGenerateJwt() {
            LoginRequestDto login = new LoginRequestDto("email@email.com", "password123");

            when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("Account is disabled"));

            assertThrows(InvalidCredentialsException.class, () -> service.login(login));

            verify(jwtService, never()).generateToken(any(), any());
        }
    }

    @Nested
    @DisplayName("Test function getUserEmailUser")
    class getEmailUser {

        @Test
        void getEmailUser_shouldReturnUserResponseDto() {
            when(repository.findByEmail(auth.getEmail())).thenReturn(Optional.of(auth));
            when(mapper.toResponseDto(auth)).thenReturn(response);

            UserResponseDto result = service.getEmailUser(auth.getEmail());

            assertNotNull(result);
            assertEquals("name", result.name());
            assertEquals("surname", result.surname());

            verify(repository).findByEmail(auth.getEmail());
            verify(mapper).toResponseDto(auth);
        }

        @Test
        void getEmailUser_shouldReturnInvalidCredentialsException() {
            when(repository.findByEmail(auth.getEmail())).thenReturn(Optional.empty());

            assertThrows(InvalidCredentialsException.class, (() -> service.getEmailUser(auth.getEmail())));

            verify(repository).findByEmail(auth.getEmail());
            verify(mapper, never()).toResponseDto(auth);
        }
    }

    @Nested
    @DisplayName("Test function updateuserProfile")
    class UpdateUserProfileTest {

        @Test
        void updateUserProfile_WhenEmailExists_ShouldUpdateAndReturnResponse() {
            // Arrange
            String email = "carlos@example.com";
            RegisterRequestDto updateRequestDto = new RegisterRequestDto("nuevo@example.com", "newPassword",
                    "Carlos Updated", "Gómez Updated");

            Auth existingAuth = new Auth();
            existingAuth.setEmail(email);
            existingAuth.setName("Carlos");
            existingAuth.setSurname("Gómez");
            existingAuth.setPassword("oldHash");

            Auth savedAuth = new Auth();
            savedAuth.setEmail("nuevo@example.com");
            savedAuth.setName("Carlos Updated");
            savedAuth.setSurname("Gómez Updated");
            savedAuth.setPassword("encodedNewPassword");
            savedAuth.setRole(TypeRole.ROLE_USER);

            UserResponseDto expectedResponse = new UserResponseDto(1L, "nuevo@example.com", "Carlos Updated",
                    "Gómez Updated", TypeRole.ROLE_USER, LocalDateTime.now(), LocalDateTime.now());

            when(repository.findByEmail(email)).thenReturn(Optional.of(existingAuth));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            when(repository.save(existingAuth)).thenReturn(savedAuth);
            when(mapper.toResponseDto(savedAuth)).thenReturn(expectedResponse);

            // Act
            UserResponseDto result = service.updateUserProfile(email, updateRequestDto);

            // Assert
            assertNotNull(result);
            assertEquals("nuevo@example.com", result.email());
            assertEquals("Carlos Updated", result.name());
            assertEquals("Gómez Updated", result.surname());

            verify(repository).findByEmail(email);
            verify(passwordEncoder).encode("newPassword");
            verify(repository).save(existingAuth);
            verify(mapper).toResponseDto(savedAuth);
        }

        @Test
        void updateUserProfile_WhenEmailDoesNotExist_ShouldThrowException() {
            // Arrange
            String email = "noexiste@example.com";

            when(repository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                    () -> service.updateUserProfile(email, requestDto));

            assertEquals("Credenciales no validas", exception.getMessage());
            verify(repository).findByEmail(email);
            verify(repository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    @DisplayName("Test function verifyEmail()")
    class VerifyEmail {

        String tokenStr = "non-existent-token";
        String tokenHash = "c6f82e93b9a87376355a9fe977200ba52a18bb46beacbe847845717fd8491014";
        VerifyEmailRequest request = new VerifyEmailRequest(tokenStr);

        @Test
        void verifyEmail_WhentokenIsValid_ShouldVerifySuccessfully() {

            VerificationToken token = new VerificationToken();
            token.setTokenHash(tokenHash);
            token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            token.setUsedAt(null);
            token.setUser(auth);

            when(verificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));

            boolean result = service.verifyEmail(request);

            assertTrue(result);
            assertNotNull(token.getUsedAt());
            assertTrue(auth.isEmailVerified());
            verify(verificationTokenRepository).findByTokenHash(tokenHash);
        }

        @Test
        void verifyEmail_WhenTokenNotFound_ShouldThrowInvalidTokenException() {

            when(verificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> service.verifyEmail(request));
            assertFalse(auth.isEmailVerified());
            verify(verificationTokenRepository).findByTokenHash(tokenHash);
        }

        @Test
        void verifyEmail_WhenTokenExpired_ShouldThrowExpiredTokenException() {

            VerificationToken token = new VerificationToken();
            token.setTokenHash(tokenHash);
            token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
            token.setUsedAt(null);
            token.setUser(auth);

            when(verificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));

            assertThrows(ExpiredTokenException.class, () -> service.verifyEmail(request));
            assertFalse(auth.isEmailVerified());
            assertNull(token.getUsedAt());
        }

        @Test
        void verifyEmail_WhenTokenAlreadyUsed_ShouldThrowInvalidTokenException() {

            VerificationToken token = new VerificationToken();
            token.setTokenHash(tokenHash);
            token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            token.setUsedAt(LocalDateTime.now().minusMinutes(5));
            token.setUser(auth);

            when(verificationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));

            assertThrows(InvalidTokenException.class, () -> service.verifyEmail(request));
            assertFalse(auth.isEmailVerified());
        }
    }

}
