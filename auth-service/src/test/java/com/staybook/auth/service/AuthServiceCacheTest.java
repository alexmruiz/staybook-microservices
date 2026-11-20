package com.staybook.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserBookingResponseDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.exception.InvalidCredentialsException;
import com.staybook.auth.mapper.AuthMapper;
import com.staybook.auth.repository.AuthRepository;
import com.staybook.auth.repository.VerificationTokenRepository;
import com.staybook.auth.security.JwtService;

@SpringJUnitConfig(AuthServiceCacheTest.TestConfiguration.class)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-cache-tests-that-is-long-enough",
        "jwt.expiration=3600000"
})
class AuthServiceCacheTest {

    private static final Long USER_ID = 42L;

    @org.springframework.beans.factory.annotation.Autowired
    private AuthService authService;

    @org.springframework.beans.factory.annotation.Autowired
    private CacheManager cacheManager;

    @org.springframework.beans.factory.annotation.Autowired
    private AuthRepository repository;

    @org.springframework.beans.factory.annotation.Autowired
    private AuthMapper mapper;

    @org.springframework.beans.factory.annotation.Autowired
    private PasswordEncoder passwordEncoder;

    private Auth user;
    private UserResponseDto userResponse;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("users").clear();
        cacheManager.getCache("usersbooking").clear();
        reset(repository, mapper, passwordEncoder);

        user = new Auth("User@Example.com", "Carlos", "Gómez");
        user.setId(USER_ID);
        user.setRole(TypeRole.ROLE_USER);
        userResponse = new UserResponseDto(
                USER_ID,
                "User@Example.com",
                "Carlos",
                "Gómez",
                TypeRole.ROLE_USER,
                null,
                null);
    }

    @Test
    void getEmailUser_WhenCalledWithDifferentEmailCase_ShouldUseSameCacheEntry() {
        when(repository.findByEmail("User@Example.com")).thenReturn(Optional.of(user));
        when(mapper.toResponseDto(user)).thenReturn(userResponse);

        UserResponseDto firstResult = authService.getEmailUser("User@Example.com");
        UserResponseDto secondResult = authService.getEmailUser("user@example.com");

        assertEquals(userResponse, firstResult);
        assertEquals(userResponse, secondResult);
        verify(repository, times(1)).findByEmail("User@Example.com");
        verify(mapper, times(1)).toResponseDto(user);
    }

    @Test
    void getUserById_WhenCalledTwice_ShouldUseCacheOnSecondCall() {
        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserBookingResponseDto firstResult = authService.getUserById(USER_ID);
        UserBookingResponseDto secondResult = authService.getUserById(USER_ID);

        assertEquals(firstResult, secondResult);
        verify(repository, times(1)).findById(USER_ID);
    }

    @Test
    void updateUserProfile_WhenEmailChanges_ShouldReplaceEmailCacheAndEvictBookingCache() {
        RegisterRequestDto request = new RegisterRequestDto(
                "new@example.com",
                "new-password",
                "Carlos Updated",
                "Gómez Updated");
        Auth updatedUser = new Auth("new@example.com", "Carlos Updated", "Gómez Updated");
        updatedUser.setId(USER_ID);
        updatedUser.setRole(TypeRole.ROLE_USER);
        UserResponseDto updatedResponse = new UserResponseDto(
                USER_ID,
                "new@example.com",
                "Carlos Updated",
                "Gómez Updated",
                TypeRole.ROLE_USER,
                null,
                null);

        when(repository.findByEmail("User@Example.com"))
                .thenReturn(Optional.of(user), Optional.of(user));
        when(repository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(repository.findById(USER_ID))
                .thenReturn(Optional.of(user), Optional.of(updatedUser));
        when(mapper.toResponseDto(user)).thenReturn(userResponse);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");
        when(repository.save(user)).thenReturn(updatedUser);
        when(mapper.toResponseDto(updatedUser)).thenReturn(updatedResponse);

        authService.getEmailUser("User@Example.com");
        authService.getUserById(USER_ID);

        UserResponseDto result = authService.updateUserProfile("User@Example.com", request);

        assertEquals(updatedResponse, result);
        assertEquals(updatedResponse, authService.getEmailUser("new@example.com"));
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.getEmailUser("user@example.com"));
        assertEquals(
                new UserBookingResponseDto(USER_ID, "new@example.com", "Carlos Updated", "Gómez Updated"),
                authService.getUserById(USER_ID));

        verify(repository, times(2)).findByEmail("User@Example.com");
        verify(repository).findByEmail("user@example.com");
        verify(repository, times(2)).findById(USER_ID);
        verify(repository).save(user);
    }

    @Configuration
    @EnableCaching(proxyTargetClass = true)
    static class TestConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("users", "usersbooking");
        }

        @Bean
        AuthRepository repository() {
            return mock(AuthRepository.class);
        }

        @Bean
        VerificationTokenRepository verificationTokenRepository() {
            return mock(VerificationTokenRepository.class);
        }

        @Bean
        AuthMapper mapper() {
            return mock(AuthMapper.class);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }

        @Bean
        AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }

        @Bean
        JwtService jwtService() {
            return mock(JwtService.class);
        }

        @Bean
        org.springframework.context.ApplicationEventPublisher applicationEventPublisher() {
            return mock(org.springframework.context.ApplicationEventPublisher.class);
        }

        @Bean
        AuthService authService(
                AuthRepository repository,
                AuthMapper mapper,
                PasswordEncoder passwordEncoder,
                AuthenticationManager authenticationManager,
                JwtService jwtService,
                VerificationTokenRepository verificationTokenRepository,
                org.springframework.context.ApplicationEventPublisher applicationEventPublisher) {
            return new AuthService(
                    repository,
                    mapper,
                    passwordEncoder,
                    authenticationManager,
                    jwtService,
                    verificationTokenRepository,
                    applicationEventPublisher);
        }
    }
}
