package com.staybook.notifications.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class AuthEventsConfig {
     public static final String AUTH_EVENTS_EXCHANGE = "auth.events.exchange";

    public static final String AUTH_EVENTS_QUEUE = "notifications.auth.events";

    @Bean
    TopicExchange authEventsExchange() {
        return new TopicExchange(AUTH_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    Queue authEventsQueue() {
        return QueueBuilder.durable(AUTH_EVENTS_QUEUE).build();
    }

    @Bean
    Binding authConfirmedBinding(Queue authEventsQueue, TopicExchange authEventsExchange) {
        return BindingBuilder.bind(authEventsQueue).to(authEventsExchange).with("auth.verify");
    }
}
