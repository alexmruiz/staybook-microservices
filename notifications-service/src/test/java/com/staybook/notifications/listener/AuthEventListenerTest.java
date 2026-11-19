package com.staybook.notifications.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staybook.notifications.dto.AuthEventDto;
import com.staybook.notifications.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthEventListenerTest {

    @Mock
    private AuthService authService;

    private AuthEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new AuthEventListener(authService);
    }

    @Test
    void onAuthEvent_whenEventIsValid_shouldDelegateToAuthService() {
        AuthEventDto event = createEvent();

        listener.onAuthEvent(event);

        verify(authService).prepareDataForSending(event);
    }

    @Test
    void onAuthEvent_whenAuthServiceFails_shouldPropagateException() {
        AuthEventDto event = createEvent();
        IllegalStateException serviceException = new IllegalStateException(
                "No se pudo preparar la notificación");
        doThrow(serviceException).when(authService).prepareDataForSending(event);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> listener.onAuthEvent(event));

        assertEquals(serviceException, exception);
        verify(authService).prepareDataForSending(event);
    }

    private AuthEventDto createEvent() {
        return new AuthEventDto(
                "event-auth-123",
                "auth.verify",
                1,
                "customer@example.com",
                "token-123",
                Instant.parse("2026-11-19T09:00:00Z"));
    }
}
