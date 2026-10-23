package com.staybook.notifications.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.entity.ProcessedEvent;
import com.staybook.notifications.enums.BookingEventType;
import com.staybook.notifications.repository.ProcessedEventRepository;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTest {

    private static final String FROM = "no-reply@staybook.com";
    private static final String RECIPIENT = "customer@example.com";

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    private NotificationsService service;

    @BeforeEach
    void setUp() {
        service = new NotificationsService(mailSender, processedEventRepository, FROM, RECIPIENT);
    }

    @Test
    void sendBookingStatus_whenConfirmed_shouldSaveEventAndSendConfirmationEmail() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CONFIRMED);
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(false);

        service.sendBookingStatus(event);

        ProcessedEvent savedEvent = captureSavedEvent();
        assertEquals(event.eventId(), savedEvent.getEventId());
        assertNotNull(savedEvent.getProcessedAt());

        SimpleMailMessage sentMessage = captureSentMessage();
        assertEquals(FROM, sentMessage.getFrom());
        assertEquals(RECIPIENT, sentMessage.getTo()[0]);
        assertEquals("Reserva confirmada - StayBook", sentMessage.getSubject());
        assertEquals(
                "Su reserva SB-ABC12345 ha sido confirmada.%nEntrada: 2026-11-05%nSalida: 2026-11-07"
                        .formatted(),
                sentMessage.getText());
        verifySaveHappensBeforeMailSend();
    }

    @Test
    void sendBookingStatus_whenCancelled_shouldSaveEventAndSendCancellationEmail() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CANCELLED);
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(false);

        service.sendBookingStatus(event);

        ProcessedEvent savedEvent = captureSavedEvent();
        assertEquals(event.eventId(), savedEvent.getEventId());
        assertNotNull(savedEvent.getProcessedAt());

        SimpleMailMessage sentMessage = captureSentMessage();
        assertEquals(FROM, sentMessage.getFrom());
        assertEquals(RECIPIENT, sentMessage.getTo()[0]);
        assertEquals("Reserva cancelada - StayBook", sentMessage.getSubject());
        assertEquals(
                "Su reserva SB-ABC12345 ha sido cancelada.%nEntrada: 2026-11-05%nSalida: 2026-11-07"
                        .formatted(),
                sentMessage.getText());
        verifySaveHappensBeforeMailSend();
    }

    @Test
    void sendBookingStatus_whenEventWasAlreadyProcessed_shouldNotSaveOrSendEmail() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CONFIRMED);
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(true);

        service.sendBookingStatus(event);

        verify(processedEventRepository).existsByEventId(event.eventId());
        verify(processedEventRepository, never()).saveAndFlush(any(ProcessedEvent.class));
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendBookingStatus_whenSavingProcessedEventFails_shouldPropagateAndNotSendEmail() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CONFIRMED);
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(false);
        doThrow(new IllegalStateException("No se pudo guardar el evento"))
                .when(processedEventRepository).saveAndFlush(any(ProcessedEvent.class));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.sendBookingStatus(event));

        assertEquals("No se pudo guardar el evento", exception.getMessage());
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendBookingStatus_whenSendingEmailFails_shouldPropagateException() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CONFIRMED);
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(false);
        doThrow(new MailSendException("SMTP no disponible"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailSendException.class, () -> service.sendBookingStatus(event));

        verify(processedEventRepository).saveAndFlush(any(ProcessedEvent.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    private ProcessedEvent captureSavedEvent() {
        ArgumentCaptor<ProcessedEvent> eventCaptor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).saveAndFlush(eventCaptor.capture());
        return eventCaptor.getValue();
    }

    private SimpleMailMessage captureSentMessage() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        return messageCaptor.getValue();
    }

    private void verifySaveHappensBeforeMailSend() {
        InOrder inOrder = inOrder(processedEventRepository, mailSender);
        inOrder.verify(processedEventRepository).saveAndFlush(any(ProcessedEvent.class));
        inOrder.verify(mailSender).send(any(SimpleMailMessage.class));
    }

    private BookingEventDto createEvent(BookingEventType eventType) {
        return new BookingEventDto(
                "event-123",
                eventType,
                1,
                Instant.parse("2026-10-23T08:00:00Z"),
                42L,
                "SB-ABC12345",
                7L,
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 7));
    }
}
