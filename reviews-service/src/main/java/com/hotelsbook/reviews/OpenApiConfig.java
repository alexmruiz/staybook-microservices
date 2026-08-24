package com.hotelsbook.reviews;

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
                        .title("StayBook - Reviews Service API")
                        .version("1.0.0")
                        .description("Microservicio para la gestión de valoraciones y reseñas de hoteles."));
    }
}
