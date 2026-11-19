package com.staybook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
    @NotBlank String token
) {
    
}
