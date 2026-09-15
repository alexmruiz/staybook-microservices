package com.hotelsbook.services.com_hotelsbook_services.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequestDto(
        @NotBlank  String street,
        @NotBlank String streetNumber,
        @NotBlank String postalCode,
        @NotNull @Valid CityRequestDto city
) {
}