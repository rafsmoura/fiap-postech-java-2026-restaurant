package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Troca de senha (endpoint exclusivo)")
public record PasswordChangeRequest(
        @Schema(example = "SenhaForte123")
        @NotBlank String currentPassword,
        @Schema(example = "NovaSenhaForte456")
        @NotBlank @Size(min = 8, max = 128) String newPassword
) {
}
