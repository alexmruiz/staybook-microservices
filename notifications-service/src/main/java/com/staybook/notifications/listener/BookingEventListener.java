package com.staybook.notifications.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.service.NotificationsService;

import jakarta.validation.Valid;

@Component
public class BookingEventListener {

    private final NotificationsService notificationService;
    public static final String BOOKING_EVENTS_QUEUE = "notifications.booking.events";

    public BookingEventListener(NotificationsService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = BOOKING_EVENTS_QUEUE   )
    public void onBookingEvent(@Payload @Valid BookingEventDto event) {
       notificationService.sendBookingStatus(event);
    }
}
