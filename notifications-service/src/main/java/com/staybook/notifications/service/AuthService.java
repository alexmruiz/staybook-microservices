package com.staybook.notifications.service;

import java.time.Instant;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.staybook.notifications.dto.AuthEventDto;
import com.staybook.notifications.entity.ProcessedEvent;
import com.staybook.notifications.repository.ProcessedEventRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service 
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final ProcessedEventRepository processedEventRepository;
    private final String from;
    private final String verificationUrl;

    private final EmailService emailService;

    public AuthService(ProcessedEventRepository processedEventRepository,
            @Value("${notifications.mail.from}") String from,
            @Value("${notifications.verification-url:http://localhost:3000/verify-email}") String verificationUrl,
            EmailService emailService) {
        this.processedEventRepository = processedEventRepository;
        this.from = from;
        this.verificationUrl = verificationUrl;
        this.emailService = emailService;
    }

    @Transactional
    public void prepareDataForSending(AuthEventDto event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("Evento duplicado ignorado: {}", event.eventId());
            return;
        }
        processedEventRepository.saveAndFlush(new ProcessedEvent(event.eventId(), Instant.now()));
        emailService.sendEmail(from, event.email(), "Confirme su cuenta", bodyFor(event));
    }

    private String bodyFor(AuthEventDto event) {
        String link = verificationUrl + "?token="
                + URLEncoder.encode(event.token(), StandardCharsets.UTF_8);

        return "Hola.\n\n"
                + "Haga clic en el siguiente enlace para verificar su cuenta:\n"
                + link;
    }
}
