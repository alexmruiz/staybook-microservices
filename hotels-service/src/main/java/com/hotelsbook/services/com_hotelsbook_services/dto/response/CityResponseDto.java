package com.hotelsbook.services.com_hotelsbook_services.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta de una ciudad")
public record CityResponseDto(

    @Schema(description = "Identificador único autogenerado", example = "1")
    Long id,

    @Schema(description = "Nombre de la ciudad", example = "Madrid")
    String name,

    @Schema(description = "País al que pertenece la ciudad", example = "España")
    String country
) {}
