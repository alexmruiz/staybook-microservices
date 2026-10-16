package com.staybook.booking.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.staybook.booking.dto.event.BookingEventDto;

@Component 
public class RabbitMQBookingEventPublisher implements BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQBookingEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "booking.events.exchange";

    public RabbitMQBookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(BookingEventDto event) {

        String routingKey = "booking" + event.eventType();

        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, event);
            log.info("Evento publicado con éxito: id={}, type={}", event.eventId(), event.eventType());
        } catch (Exception e) {
            log.error("Error al publicar el evento {}: {}", event.eventId(), e.getMessage());
        }
    }

}
