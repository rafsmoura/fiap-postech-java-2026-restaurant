package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Atualização dos dados do usuário (sem senha)")
public record UserUpdateRequest(
        @Schema(example = "Maria Silva Santos")
        @NotBlank String name,
        @Schema(example = "maria.nova@email.com")
        @Email @NotBlank String email,
        @Schema(example = "maria.silva")
        @NotBlank @Size(min = 3, max = 64) String login,
        @Valid @NotNull AddressRequest address
) {
}
