package com.staybook.notifications.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.staybook.notifications.dto.BookingEventDto;

@Service
public class NotificationsService {

    private final JavaMailSender mailSender;

    public NotificationsService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 
     * @param event
     * @throws MailException
     */
    public void sendBookingStatus(BookingEventDto event) throws MailException {

        SimpleMailMessage message = new SimpleMailMessage();

        String msg = switch (event.eventType()) {
        case CANCELLED -> "Su reserva se ha cancelado";
        case CONFIRMED -> "Su reserva se ha confirmado";
        };

        message.setFrom("no-reply@staybook.com");
        message.setTo("test@test.com");
        message.setText(msg);

        message.setSubject("El estado de su reserva ha cambiado - StayBook");
        mailSender.send(message);
    }
}