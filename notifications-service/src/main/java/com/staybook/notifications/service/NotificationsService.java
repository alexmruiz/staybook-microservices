package com.staybook.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.staybook.notifications.dto.BookingEventDto;

@Service
public class NotificationsService {

    private static final Logger log = LoggerFactory.getLogger(NotificationsService.class);

    public void sendBookingConfirmed(BookingEventDto event) {
        log.info("event confirmed recibido" + event);
    }

    public void sendBookingCancelled(BookingEventDto event) {
        log.info("evento cancelled recibido" + event);

    }
}
