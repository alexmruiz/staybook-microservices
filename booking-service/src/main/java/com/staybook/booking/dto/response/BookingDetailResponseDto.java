package com.staybook.booking.dto.response;

import java.util.List;

public record BookingDetailResponseDto(
    BookingResponseDto booking,
    HotelSummaryDto hotel,
    List<ReviewSummaryDto> reviews
) {
    
}
