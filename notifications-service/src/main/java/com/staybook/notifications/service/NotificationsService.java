package com.staybook.notifications.service;

import org.springframework.mail.SimpleMailMessage;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.staybook.notifications.client.AuthClient;
import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.dto.UserSummaryResponseDto;
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
    private final AuthClient authClient;

    public NotificationsService(JavaMailSender mailSender,
            ProcessedEventRepository processedEventRepository,
            @Value("${notifications.mail.from}") String from,
            AuthClient authClient) {
        this.mailSender = mailSender;
        this.processedEventRepository = processedEventRepository;
        this.from = from;
        this.authClient = authClient;
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
        UserSummaryResponseDto user = authClient.getUserById(event.userId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.email());
        message.setSubject(subjectFor(event.eventType()));
        message.setText(bodyFor(event, user));
        return message;
    }

    private String subjectFor(BookingEventType type) {
        return switch (type) {
            case BOOKING_CONFIRMED -> "Reserva confirmada - StayBook";
            case BOOKING_CANCELLED -> "Reserva cancelada - StayBook";
        };
    }

    private String bodyFor(BookingEventDto event, UserSummaryResponseDto user) {
        String action = switch (event.eventType()) {
            case BOOKING_CONFIRMED -> "confirmada";
            case BOOKING_CANCELLED -> "cancelada";
        };

        return "Hola %s %s. Su reserva %s ha sido %s.%nEntrada: %s%nSalida: %s"
                .formatted(user.name(), user.surname(), event.bookingReference(), action,
                        event.checkInDate(), event.checkOutDate());
    }
}