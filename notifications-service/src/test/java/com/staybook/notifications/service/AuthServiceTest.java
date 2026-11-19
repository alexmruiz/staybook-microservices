package com.staybook.notifications.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staybook.notifications.dto.AuthEventDto;
import com.staybook.notifications.entity.ProcessedEvent;
import com.staybook.notifications.repository.ProcessedEventRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String FROM = "no-reply@staybook.com";
    private static final String VERIFICATION_URL = "http://localhost:3000/verify-email";

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private EmailService emailService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(processedEventRepository, FROM, VERIFICATION_URL, emailService);
    }

    @Test
    void prepareDataForSending_whenEventIsNew_shouldSaveEventAndSendVerificationEmail() {
        AuthEventDto event = createEvent("abc+123/==");
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(false);

        authService.prepareDataForSending(event);

        ArgumentCaptor<ProcessedEvent> savedEvent = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).saveAndFlush(savedEvent.capture());
        assertEquals(event.eventId(), savedEvent.getValue().getEventId());
        assertNotNull(savedEvent.getValue().getProcessedAt());

        verify(emailService).sendEmail(
                FROM,
                event.email(),
                "Confirme su cuenta",
                "Hola.\n\n"
                        + "Haga clic en el siguiente enlace para verificar su cuenta:\n"
                        + VERIFICATION_URL
                        + "?token=abc%2B123%2F%3D%3D");
    }

    @Test
    void prepareDataForSending_whenSavingEventFails_shouldPropagateExceptionAndNotSendEmail() {
        AuthEventDto event = createEvent("token-123");
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(false);
        doThrow(new IllegalStateException("No se pudo guardar el evento"))
                .when(processedEventRepository).saveAndFlush(any(ProcessedEvent.class));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.prepareDataForSending(event));

        assertEquals("No se pudo guardar el evento", exception.getMessage());
        verify(emailService, never()).sendEmail(any(), any(), any(), any());
    }

    private AuthEventDto createEvent(String token) {
        return new AuthEventDto(
                "event-auth-123",
                "auth.verify",
                1,
                "customer@example.com",
                token,
                Instant.parse("2026-11-19T09:00:00Z"));
    }
}
