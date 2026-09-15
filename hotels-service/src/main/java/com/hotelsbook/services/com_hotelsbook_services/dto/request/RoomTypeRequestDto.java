package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import com.hotelsbook.services.com_hotelsbook_services.entity.RoomTypeName;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RoomTypeRequestDto(
    @NotNull RoomTypeName type,
    @NotNull @Positive @Min (1) Integer quantity
) {
    
}
