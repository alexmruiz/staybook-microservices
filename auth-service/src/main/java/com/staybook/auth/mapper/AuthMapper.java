package com.staybook.auth.mapper;

import com.staybook.auth.dto.request.AuthRequestDto;
import com.staybook.auth.dto.response.AuthResponseDto;
import com.staybook.auth.entity.Auth;

public class AuthMapper {

    public Auth toEntity(AuthRequestDto request) {
        if (request == null) {
            return null;
        }

        return new Auth(request.email(), request.password(), request.name());
    }

    public AuthResponseDto toResponseDto(Auth auth) {
        if (auth == null) {
            return null;
        }

        return new AuthResponseDto(auth.getId(), auth.getEmail(), auth.getName(), auth.getSurname(), auth.getRole(),
                auth.getCreatedAt(), auth.getUpdatedAt());
    }
}
