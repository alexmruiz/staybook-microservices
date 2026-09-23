package com.staybook.booking.mapper;

import org.springframework.stereotype.Component;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.entity.Booking;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDto dto) {
        if (dto == null)
            return null;

        return new Booking(dto.userId(), dto.hotelId(), dto.roomTypeId(), dto.checkInDate(), dto.checkOutDate());
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null)
            return null;

        return new BookingResponseDto(
                booking.getId(),
                booking.getUserId(),
                booking.getHotelId(),
                booking.getRoomTypeId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCreatedAt(),
                booking.getUpdatedAt(),
                booking.getBookingReference());
    }
}
