package com.hotelsbook.services.com_hotelsbook_services.mapper;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.RoomTypeRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.RoomTypeResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.RoomType;

public class RoomTypeMapper {

    public RoomType toEntity(RoomTypeRequestDto dto) {
        if (dto == null)
            return null;

        return new RoomType(dto.type(), dto.quantity(), null);
    }

    public RoomTypeResponseDto toResponseDto(RoomType entity) {
        if (entity == null)
            return null;

        return new RoomTypeResponseDto(entity.getId(), entity.getType(), entity.getQuantity());

    }
}
