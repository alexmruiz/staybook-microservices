package com.staybook.notifications.events;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.service.NotificationsService;

@Component 
public class BookingEventListener {

    private final NotificationsService notificationService;

    public BookingEventListener(NotificationsService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener (queues = "notifications.booking.events")
    public void onBookingEvent(BookingEventDto event) {
        switch (event.eventType()) {
            case CONFIRMED -> notificationService.sendBookingConfirmed(event);
            case CANCELLED -> notificationService.sendBookingCancelled(event);
        }
    }
}
