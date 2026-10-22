package com.staybook.notifications.events;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.service.NotificationsService;

@Component
public class BookingEventListener {

    private final NotificationsService notificationService;
    public static final String BOOKING_EVENTS = "notifications.booking.events";

    public BookingEventListener(NotificationsService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = BOOKING_EVENTS)
    public void onBookingEvent(BookingEventDto event) {
       notificationService.sendBookingStatus(event);
    }
}
