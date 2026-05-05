package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da validação de login")
public record LoginResponse(
        @Schema(example = "true") boolean valid,
        @Schema(example = "1") Long userId,
        @Schema(example = "CLIENT") UserType userType,
        @Schema(example = "Login válido") String message
) {
}
