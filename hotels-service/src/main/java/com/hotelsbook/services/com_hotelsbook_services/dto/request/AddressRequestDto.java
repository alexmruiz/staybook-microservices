package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddressRequestDto(
        @NotNull String street,
        @NotNull String streetNumber,
        @NotNull String postalCode,
        @NotNull CityRequestDto city
) {
}