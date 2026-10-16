package com.staybook.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMqConfig {

        public static final String BOOKING_EVENTS_EXCHANGE = "booking.events.exchange";

        public static final String BOOKING_EVENTS_QUEUE = "notifications.booking.events";

        @Bean
        TopicExchange bookingEventsExchange() {
                return new TopicExchange(BOOKING_EVENTS_EXCHANGE, true, false);
        }

        @Bean
        Queue bookingEventsQueue() {
                return QueueBuilder.durable(BOOKING_EVENTS_QUEUE).build();
        }

        @Bean
        Binding bookingEventsBinding(Queue bookingEventsQueue, TopicExchange bookingEventsExchange) {
                return BindingBuilder.bind(bookingEventsQueue).to(bookingEventsExchange).with("booking.#");
        }

        @Bean
        MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
                ObjectMapper rabbitObjectMapper = objectMapper.copy();
                rabbitObjectMapper.findAndRegisterModules();
                rabbitObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                return new Jackson2JsonMessageConverter(rabbitObjectMapper);
        }
}
