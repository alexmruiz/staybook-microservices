package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import jakarta.validation.constraints.NotNull;

public record AmenityRequestDto(
    @NotNull String name,
    String description
) {
    
}
