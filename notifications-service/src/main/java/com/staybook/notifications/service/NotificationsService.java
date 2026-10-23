package com.staybook.notifications.service;

import org.springframework.mail.SimpleMailMessage;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.entity.ProcessedEvent;
import com.staybook.notifications.enums.BookingEventType;
import com.staybook.notifications.repository.ProcessedEventRepository;

import jakarta.transaction.Transactional;

@Service
public class NotificationsService {

    private static final Logger log = LoggerFactory.getLogger(NotificationsService.class);

    private final JavaMailSender mailSender;
    private final ProcessedEventRepository processedEventRepository;
    private final String from;
    private final String defaultRecipient;

    public NotificationsService(JavaMailSender mailSender,
            ProcessedEventRepository processedEventRepository,
            @Value("${notifications.mail.from}") String from,
            @Value("${notifications.mail.default-recipient}") String defaultRecipient) {
        this.mailSender = mailSender;
        this.processedEventRepository = processedEventRepository;
        this.from = from;
        this.defaultRecipient = defaultRecipient;
    }

    @Transactional 
    public void sendBookingStatus(BookingEventDto event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("Evento duplicado ignorado: {}", event.eventId());
            return;
        }
        processedEventRepository.saveAndFlush(
                new ProcessedEvent(event.eventId(), Instant.now()));
        mailSender.send(buildMessage(event));
    }

    private SimpleMailMessage buildMessage(BookingEventDto event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(defaultRecipient);
        message.setSubject(subjectFor(event.eventType()));
        message.setText(bodyFor(event));
        return message;
    }

    private String subjectFor(BookingEventType type) {
        return switch (type) {
            case BOOKING_CONFIRMED -> "Reserva confirmada - StayBook";
            case BOOKING_CANCELLED -> "Reserva cancelada - StayBook";
        };
    }

    private String bodyFor(BookingEventDto event) {
        String action = switch (event.eventType()) {
            case BOOKING_CONFIRMED -> "confirmada";
            case BOOKING_CANCELLED -> "cancelada";
        };
        return "Su reserva %s ha sido %s.%nEntrada: %s%nSalida: %s"
                .formatted(event.bookingReference(), action,
                        event.checkInDate(), event.checkOutDate());
    }
}