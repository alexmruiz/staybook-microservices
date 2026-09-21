package com.staybook.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomAvailabilityResponseDto(
    Long id,
    Long hotelId,
    Long roomTypeId,
    LocalDate date,
    Integer availableQuantity,
    BigDecimal price
) {}
