package com.staybook.booking.dto.response;

import java.util.List;

public record HotelSummaryDto(
                Long id,
                String name,
                Integer stars,
                List<RoomTypeSumaryDto> roomTypes) {

}
