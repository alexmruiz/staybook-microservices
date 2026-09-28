package com.staybook.auth.mapper;

import org.springframework.stereotype.Component;

import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;

@Component 
public class AuthMapper {

    public Auth toEntity(RegisterRequestDto request) {
        if (request == null) {
            return null;
        }

        return new Auth(request.email(), request.name(), request.surname());
    }

    public UserResponseDto toResponseDto(Auth auth) {
        if (auth == null) {
            return null;
        }

        return new UserResponseDto(auth.getId(), auth.getEmail(), auth.getName(), auth.getSurname(), auth.getRole(),
                auth.getCreatedAt(), auth.getUpdatedAt());
    }
}
