package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record HotelRequestDto(
    @NotNull String name,
    String description,
    @NotNull @Valid AddressRequestDto address,
    @NotNull @Positive Integer stars,
    @Positive Integer capacity,
    @NotNull @Valid Set<RoomTypeRequestDto> roomTypes,
    Set<Long> amenitiesIds 
) {
    
}
