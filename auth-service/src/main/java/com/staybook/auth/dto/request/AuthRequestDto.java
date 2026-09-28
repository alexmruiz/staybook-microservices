package com.staybook.auth.dto.request;

public record AuthRequestDto(
        String email,
        String password,
        String name,
        String surname) {
}