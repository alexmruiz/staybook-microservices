package com.hotelsbook.services.com_hotelsbook_services.dto.response;

public record AddressResponseDto(
    Long id,
    String street,
    String streetNumber,
    String postalCode,
    CityResponseDto city
) {
    
}
