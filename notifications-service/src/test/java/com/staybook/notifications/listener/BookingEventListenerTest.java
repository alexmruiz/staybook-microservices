package com.staybook.notifications.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.enums.BookingEventType;
import com.staybook.notifications.service.BookingService;

@ExtendWith(MockitoExtension.class)
class BookingEventListenerTest {

    @Mock
    private BookingService bookingService;

    private BookingEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new BookingEventListener(bookingService);
    }

    @Test
    void onBookingEvent_whenEventIsValid_shouldDelegateToBookingService() {
        BookingEventDto event = createEvent();

        listener.onBookingEvent(event);

        verify(bookingService).sendBookingStatus(event);
    }

    @Test
    void onBookingEvent_whenBookingServiceFails_shouldPropagateException() {
        BookingEventDto event = createEvent();
        IllegalStateException serviceException = new IllegalStateException("No se pudo enviar la notificación");
        doThrow(serviceException).when(bookingService).sendBookingStatus(event);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> listener.onBookingEvent(event));

        assertEquals(serviceException, exception);
        verify(bookingService).sendBookingStatus(event);
    }

    private BookingEventDto createEvent() {
        return new BookingEventDto(
                "event-123",
                BookingEventType.BOOKING_CONFIRMED,
                1,
                Instant.parse("2026-10-23T08:00:00Z"),
                42L,
                "SB-ABC12345",
                7L,
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 7));
    }
}
