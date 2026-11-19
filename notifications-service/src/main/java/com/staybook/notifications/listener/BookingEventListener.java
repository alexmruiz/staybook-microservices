package com.staybook.notifications.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.service.BookingService;

import jakarta.validation.Valid;

@Component
public class BookingEventListener {

    private final BookingService bookingService;
    public static final String BOOKING_EVENTS_QUEUE = "notifications.booking.events";

    public BookingEventListener(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @RabbitListener(queues = BOOKING_EVENTS_QUEUE   )
    public void onBookingEvent(@Payload @Valid BookingEventDto event) {
       bookingService.sendBookingStatus(event);
    }
}
