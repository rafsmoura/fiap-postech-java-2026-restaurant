package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Cadastro de usuário (dono de restaurante ou cliente)")
public record UserCreateRequest(
        @Schema(example = "Maria Silva")
        @NotBlank String name,
        @Schema(example = "maria@email.com")
        @Email @NotBlank String email,
        @Schema(example = "maria.silva")
        @NotBlank @Size(min = 3, max = 64) String login,
        @Schema(example = "SenhaForte123")
        @NotBlank @Size(min = 8, max = 128) String password,
        @Valid @NotNull AddressRequest address,
        @Schema(example = "CLIENT", allowableValues = {"RESTAURANT_OWNER", "CLIENT"})
        @NotNull UserType userType
) {
}
