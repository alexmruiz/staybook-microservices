package com.staybook.booking.dto.response;

import java.math.BigDecimal;

public record RoomTypeSumaryDto(
                Long id,
                String name,
                Integer capacity,
                BigDecimal basePrice) {

}
