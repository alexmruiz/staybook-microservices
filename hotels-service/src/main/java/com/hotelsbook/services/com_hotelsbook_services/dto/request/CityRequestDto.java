package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear o actualizar una ciudad")
public record CityRequestDto(
    
    @Schema(description = "Nombre de la ciudad", example = "Madrid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre de la ciudad es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String name,

    @Schema(description = "País donde se ubica la ciudad", example = "España", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El país es obligatorio")
    String country
) {}
