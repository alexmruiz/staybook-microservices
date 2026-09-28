package com.staybook.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(
        @NotNull @Email String email,
        @NotNull @Min(8) String password,
        @NotNull String name,
        @NotNull String surname) {
}