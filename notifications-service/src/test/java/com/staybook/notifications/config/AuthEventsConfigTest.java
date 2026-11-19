package com.staybook.notifications.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

class AuthEventsConfigTest {

    private final AuthEventsConfig config = new AuthEventsConfig();

    @Test
    void authEventsExchange_shouldBeDurableWithExpectedName() {
        TopicExchange exchange = config.authEventsExchange();

        assertEquals(AuthEventsConfig.AUTH_EVENTS_EXCHANGE, exchange.getName());
        assertTrue(exchange.isDurable());
        assertTrue(!exchange.isAutoDelete());
    }

    @Test
    void authEventsQueue_shouldBeDurableWithExpectedName() {
        Queue queue = config.authEventsQueue();

        assertEquals(AuthEventsConfig.AUTH_EVENTS_QUEUE, queue.getName());
        assertTrue(queue.isDurable());
        assertTrue(!queue.isExclusive());
        assertTrue(!queue.isAutoDelete());
    }

    @Test
    void authConfirmedBinding_shouldUseAuthVerifyRoutingKey() {
        Queue queue = config.authEventsQueue();
        TopicExchange exchange = config.authEventsExchange();

        Binding binding = config.authConfirmedBinding(queue, exchange);

        assertEquals(AuthEventsConfig.AUTH_EVENTS_QUEUE, binding.getDestination());
        assertEquals(AuthEventsConfig.AUTH_EVENTS_EXCHANGE, binding.getExchange());
        assertEquals("auth.verify", binding.getRoutingKey());
    }
}
