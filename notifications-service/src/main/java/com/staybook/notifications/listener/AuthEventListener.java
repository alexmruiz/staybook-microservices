package com.staybook.notifications.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.staybook.notifications.dto.AuthEventDto;
import com.staybook.notifications.service.AuthService;

import jakarta.validation.Valid;

@Component 
public class AuthEventListener {
    private final AuthService authService;
    public static final String AUTH_EVENTS_QUEUE = "notifications.auth.events";

    public AuthEventListener(AuthService authService) {
        this.authService = authService;
    }

    @RabbitListener(queues = AUTH_EVENTS_QUEUE   )
    public void onAuthEvent(@Payload @Valid AuthEventDto event) {
       authService.prepareDataForSending(event);
    }
}
