package com.hotelsbook.services.com_hotelsbook_services.dto.response;

public record CityImportResponseDto(
    String countryName,
    int insertedCount,
    int discardedCount
) {
    
}
