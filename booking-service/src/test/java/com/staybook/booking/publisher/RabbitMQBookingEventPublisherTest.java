package com.staybook.booking.publisher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.staybook.booking.config.RabbitMqConfig;
import com.staybook.booking.dto.event.BookingEventDto;
import com.staybook.booking.enums.BookingEventType;

@ExtendWith(MockitoExtension.class)
class RabbitMQBookingEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQBookingEventPublisher publisher;

    @Test
    void publish_whenBookingConfirmed_shouldSendToConfirmedRoutingKey() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CONFIRMED);

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
                RabbitMqConfig.BOOKING_EVENTS_EXCHANGE,
                "booking.confirmed",
                event);
    }

    @Test
    void publish_whenBookingCancelled_shouldSendToCancelledRoutingKey() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CANCELLED);

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
                RabbitMqConfig.BOOKING_EVENTS_EXCHANGE,
                "booking.cancelled",
                event);
    }

    @Test
    void publish_whenRabbitThrowsAmqpException_shouldHandleException() {
        BookingEventDto event = createEvent(BookingEventType.BOOKING_CONFIRMED);
        AmqpException rabbitException = new AmqpException("RabbitMQ is unavailable");

        doThrow(rabbitException).when(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.BOOKING_EVENTS_EXCHANGE),
                eq(event.eventType().routingKey()),
                eq(event));

        assertDoesNotThrow(() -> publisher.publish(event));

        verify(rabbitTemplate).convertAndSend(
                RabbitMqConfig.BOOKING_EVENTS_EXCHANGE,
                event.eventType().routingKey(),
                event);
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
