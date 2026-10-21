package com.staybook.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.staybook.notifications.dto.BookingEventDto;

@Service
public class NotificationsService {

    private static final Logger log = LoggerFactory.getLogger(NotificationsService.class);

    private final JavaMailSender mailSender;

    public NotificationsService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmed(BookingEventDto event) {
        log.info("Evento booking.confirmed recibido: {}", event);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // Dirección remitente (configurable o genérica para desarrollo)
            message.setFrom("no-reply@staybook.com");
            //message.setTo(event.getUserEmail()); 
            message.setSubject("¡Reserva Confirmada - StayBook!");
            //message.setText("Hola " + event.getUserName() + ",\n\nTu reserva ha sido confirmada con éxito. ¡Gracias por confiar en StayBook!");

            mailSender.send(message);
            //log.info("Correo de confirmación enviado correctamente a: {}", event.getUserEmail());

        } catch (MailException e) {
            log.error("Error al enviar el correo de confirmación de reserva: {}", e.getMessage(), e);
            // Dependiendo de la criticidad, puedes propagar la excepción o dejar que la reintente RabbitMQ
        }
    }

    public void sendBookingCancelled(BookingEventDto event) {
        log.info("Evento booking.cancelled recibido: {}", event);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@staybook.com");
            //message.setTo(event.getUserEmail());
            message.setSubject("Reserva Cancelada - StayBook");
            //message.setText("Hola " + event.getUserName() + ",\n\nTe informamos que tu reserva ha sido cancelada.");

            mailSender.send(message);
            //log.info("Correo de cancelación enviado correctamente a: {}", event.getUserEmail());

        } catch (MailException e) {
            log.error("Error al enviar el correo de cancelación: {}", e.getMessage(), e);
        }
    }
}