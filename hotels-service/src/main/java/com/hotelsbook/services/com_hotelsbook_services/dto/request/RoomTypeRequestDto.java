package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import com.hotelsbook.services.com_hotelsbook_services.entity.RoomTypeName;

import jakarta.validation.constraints.NotNull;

public record RoomTypeRequestDto(
    @NotNull RoomTypeName roomType,
    @NotNull Integer quantity
) {
    
}
