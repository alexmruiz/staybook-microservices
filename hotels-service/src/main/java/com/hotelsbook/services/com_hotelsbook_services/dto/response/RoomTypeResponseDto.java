package com.hotelsbook.services.com_hotelsbook_services.dto.response;

import com.hotelsbook.services.com_hotelsbook_services.entity.RoomTypeName;

public record RoomTypeResponseDto(
    Long id,
    RoomTypeName type,
    Integer quantity
) {
}
