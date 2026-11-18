package com.staybook.auth.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.staybook.auth.config.RabbitMqConfig;
import com.staybook.auth.dto.response.AuthEventDto;

@Component
public class RabbitMQAuthEventPublisher implements AuthEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQAuthEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQAuthEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(AuthEventDto event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.AUTH_EVENTS_EXCHANGE, event.eventType(), event);
            log.info("Evento publicado: id={}, type={}", event.eventId(), event.eventType());
        } catch (AmqpException e) {
            log.error("No se pudo publicar el evento {}", event.eventId(), e);
        }
    }

}
