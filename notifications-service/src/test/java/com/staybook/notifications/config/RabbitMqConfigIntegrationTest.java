package com.staybook.notifications.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staybook.notifications.dto.BookingEventDto;
import com.staybook.notifications.enums.BookingEventType;

import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
class RabbitMqConfigIntegrationTest {

    @Container
    private static final RabbitMQContainer rabbitMq = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:4-management"))
            .withUser("notifications-test", "notifications-test-password");

    private AnnotationConfigApplicationContext context;
    private RabbitTemplate rabbitTemplate;

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @ParameterizedTest
    @EnumSource(BookingEventType.class)
    void shouldRouteAndDeserializeBookingEvents(BookingEventType eventType) {
        startContext();

        BookingEventDto event = createEvent(eventType);
        String routingKey = routingKeyFor(eventType);
        rabbitTemplate.convertAndSend(RabbitMqConfig.BOOKING_EVENTS_EXCHANGE, routingKey, event);

        Message receivedMessage = rabbitTemplate.receive(RabbitMqConfig.BOOKING_EVENTS_QUEUE, 5_000);

        assertNotNull(receivedMessage, "RabbitMQ no entregó el evento a la cola configurada");
        assertEquals(routingKey, receivedMessage.getMessageProperties().getReceivedRoutingKey());
        assertEquals("application/json", receivedMessage.getMessageProperties().getContentType());
        assertEquals(event, rabbitTemplate.getMessageConverter().fromMessage(receivedMessage));
    }

    private void startContext() {
        context = new AnnotationConfigApplicationContext(TestRabbitConfig.class);
        rabbitTemplate = context.getBean(RabbitTemplate.class);
    }

    private String routingKeyFor(BookingEventType eventType) {
        return switch (eventType) {
            case BOOKING_CONFIRMED -> "booking.confirmed";
            case BOOKING_CANCELLED -> "booking.cancelled";
        };
    }

    private BookingEventDto createEvent(BookingEventType eventType) {
        return new BookingEventDto(
                "event-" + UUID.randomUUID(),
                eventType,
                1,
                Instant.parse("2026-10-23T08:00:00Z"),
                42L,
                "SB-ABC12345",
                7L,
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 7));
    }

    @Configuration
    @Import(RabbitMqConfig.class)
    static class TestRabbitConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper().findAndRegisterModules();
        }

        @Bean
        CachingConnectionFactory connectionFactory() {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                    rabbitMq.getHost(),
                    rabbitMq.getAmqpPort());
            connectionFactory.setUsername(rabbitMq.getAdminUsername());
            connectionFactory.setPassword(rabbitMq.getAdminPassword());
            return connectionFactory;
        }

        @Bean
        RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }

        @Bean
        RabbitTemplate rabbitTemplate(
                CachingConnectionFactory connectionFactory,
                org.springframework.amqp.support.converter.MessageConverter messageConverter) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(messageConverter);
            return rabbitTemplate;
        }
    }
}
