package com.hotelsbook.services.com_hotelsbook_services.dto.response;

import java.util.Set;

public record HotelResponseDto(
    Long id,
    String name,
    String description,
    Integer stars,
    Integer capacity,
    Set<RoomTypeResponseDto> roomTypes,
    Set<AmenityResponseDto> amenities
) {
}
