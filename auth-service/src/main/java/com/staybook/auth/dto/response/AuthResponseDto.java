package com.staybook.auth.dto.response;

import com.staybook.auth.enums.TypeRole;

public record AuthResponseDto(
                String token,
                String tokenType,
                long expiresIn,
                String email,
                TypeRole role) {

}