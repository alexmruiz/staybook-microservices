package com.staybook.notifications.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration 
public class RabbitMessageConverterConfig {
    @Bean
    MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        ObjectMapper rabbitObjectMapper = objectMapper.copy();
        rabbitObjectMapper.findAndRegisterModules();
        rabbitObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }
}
