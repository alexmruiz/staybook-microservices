package com.staybook.notifications.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.staybook.notifications.client.AuthClient;
import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.dto.UserSummaryResponseDto;
import com.staybook.notifications.entity.ProcessedEvent;
import com.staybook.notifications.enums.BookingEventType;
import com.staybook.notifications.repository.ProcessedEventRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final ProcessedEventRepository processedEventRepository;
    private final String from;
    private final AuthClient authClient;
    private final EmailService emailService;

    public BookingService(ProcessedEventRepository processedEventRepository,
            @Value("${notifications.mail.from}") String from, AuthClient authClient, EmailService emailService) {
        this.processedEventRepository = processedEventRepository;
        this.from = from;
        this.authClient = authClient;
        this.emailService = emailService;
    }

    @Transactional
    public void sendBookingStatus(BookingEventDto event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("Evento duplicado ignorado: {}", event.eventId());
            return;
        }
        processedEventRepository.saveAndFlush(new ProcessedEvent(event.eventId(), Instant.now()));
        UserSummaryResponseDto user = authClient.getUserById(event.userId());
        emailService.sendEmail(from, user.email(), subjectFor(event.eventType()), bodyFor(event, user));
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

        return "Hola %s %s. Su reserva %s ha sido %s.%nEntrada: %s%nSalida: %s".formatted(user.name(), user.surname(),
                event.bookingReference(), action, event.checkInDate(), event.checkOutDate());
    }
}