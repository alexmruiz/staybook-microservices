package com.hotelsbook.services.com_hotelsbook_services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StayBook - Hotels Service API")
                        .version("1.0.0")
                        .description("Microservicio para la gestión del catálogo de hoteles y sus servicios asociados."));
    }
}
