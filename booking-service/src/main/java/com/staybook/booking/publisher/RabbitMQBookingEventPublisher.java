package com.staybook.booking.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.staybook.booking.config.RabbitMqConfig;
import com.staybook.booking.dto.event.BookingEventDto;

@Component
public class RabbitMQBookingEventPublisher implements BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQBookingEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQBookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(BookingEventDto event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.BOOKING_EVENTS_EXCHANGE, event.eventType().routingKey(),
                    event);
            log.info("Evento publicado: id={}, type={}", event.eventId(), event.eventType());
        } catch (AmqpException e) {
            log.error("No se pudo publicar el evento {}", event.eventId(), e);
        }
    }

}
