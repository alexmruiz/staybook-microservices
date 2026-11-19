package com.staybook.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String AUTH_EVENTS_EXCHANGE = "auth.events.exchange";

    @Bean
    TopicExchange authEventsExchange() {
        return new TopicExchange(AUTH_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        ObjectMapper rabbitObjectMapper = objectMapper.copy();
        rabbitObjectMapper.findAndRegisterModules();
        rabbitObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }

}
