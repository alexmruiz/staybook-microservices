package com.staybook.booking.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.staybook.booking.config.RabbitMqConfig;
import com.staybook.booking.dto.event.BookingEventDto;
import com.staybook.booking.enums.BookingEventType;

import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
class RabbitMQBookingEventPublisherIntegrationTest {

    @Container
    private static final RabbitMQContainer rabbitMq = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:4-management"))
            .withUser("staybook-test", "staybook-test-password");

    private CachingConnectionFactory connectionFactory;
    private RabbitAdmin rabbitAdmin;
    private RabbitTemplate rabbitTemplate;
    private RabbitMQBookingEventPublisher publisher;

    @ParameterizedTest
    @EnumSource(BookingEventType.class)
    void publish_shouldDeliverBookingEventToBoundQueue(BookingEventType eventType) {
        configureRabbitClient();

        BookingEventDto event = createEvent(eventType);
        String queueName = "booking.publisher.integration." + UUID.randomUUID();
        Queue queue = new Queue(queueName, false, false, true);
        TopicExchange exchange = new TopicExchange(
                RabbitMqConfig.BOOKING_EVENTS_EXCHANGE,
                true,
                false);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(eventType.routingKey()));

        publisher.publish(event);

        Message receivedMessage = rabbitTemplate.receive(queueName, 5_000);
        assertNotNull(receivedMessage, "RabbitMQ no entregó el mensaje a la cola");
        assertEquals(eventType.routingKey(), receivedMessage.getMessageProperties().getReceivedRoutingKey());

        Object receivedEvent = rabbitTemplate.getMessageConverter().fromMessage(receivedMessage);
        assertEquals(event, receivedEvent);
    }

    @AfterEach
    void closeRabbitClient() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    private void configureRabbitClient() {
        connectionFactory = new CachingConnectionFactory(rabbitMq.getHost(), rabbitMq.getAmqpPort());
        connectionFactory.setUsername(rabbitMq.getAdminUsername());
        connectionFactory.setPassword(rabbitMq.getAdminPassword());

        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        rabbitAdmin = new RabbitAdmin(connectionFactory);
        publisher = new RabbitMQBookingEventPublisher(rabbitTemplate);
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
}
