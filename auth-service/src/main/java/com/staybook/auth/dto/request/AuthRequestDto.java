package com.staybook.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthRequestDto(
        @NotNull String email,
        @NotNull String password,
        @NotNull String name,
        @NotNull String surname) {
}