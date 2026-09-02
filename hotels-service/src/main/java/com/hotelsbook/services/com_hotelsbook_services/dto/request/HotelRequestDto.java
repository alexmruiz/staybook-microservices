package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record HotelRequestDto(
    @NotNull String name,
    String description,
    @NotNull AddressRequestDto address,
    @Positive Integer stars,
    @Positive Integer capacity,
    @NotNull Set<RoomTypeRequestDto> roomTypes,
    Set<Long> amenitiesIds 
) {
    
}
