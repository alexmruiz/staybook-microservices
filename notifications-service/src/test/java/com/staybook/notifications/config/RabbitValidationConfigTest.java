package com.staybook.notifications.config;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class RabbitValidationConfigTest {

    @Test
    void configureRabbitListeners_shouldRegisterConfiguredValidator() {
        LocalValidatorFactoryBean validator = Mockito.mock(LocalValidatorFactoryBean.class);
        RabbitListenerEndpointRegistrar registrar = Mockito.mock(RabbitListenerEndpointRegistrar.class);

        RabbitValidationConfig config = new RabbitValidationConfig(validator);

        config.configureRabbitListeners(registrar);

        verify(registrar).setValidator(validator);
    }
}
