package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para validação de login")
public record LoginRequest(
        @Schema(example = "maria.silva")
        @NotBlank String login,
        @Schema(example = "SenhaForte123")
        @NotBlank String password
) {
}
