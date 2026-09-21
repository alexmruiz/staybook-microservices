package com.staybook.booking.mapper;

import com.staybook.booking.dto.request.RoomAvailabilityRequestDto;
import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.entity.RoomAvailability;

public class RoomAvailabilityMapper {

    public RoomAvailability toEntity(RoomAvailabilityRequestDto requestDto) {
        if (requestDto == null)
            return null;

        return new RoomAvailability(requestDto.hotelId(), requestDto.roomTypeId(),
                requestDto.date(), requestDto.availableQuantity(), requestDto.totalPrice());
    }

    public RoomAvailabilityResponseDto toResponseDto(RoomAvailability roomAvailability) {
        if (roomAvailability == null)
            return null;

        return new RoomAvailabilityResponseDto(roomAvailability.getId(), roomAvailability.getHotelId(),
                roomAvailability.getRoomTypeId(), roomAvailability.getDate(), roomAvailability.getAvailableQuantity(),
                roomAvailability.getPrice());
    }
}
