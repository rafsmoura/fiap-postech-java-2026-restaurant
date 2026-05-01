package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Endereço (rua, número, cidade, CEP)")
public record AddressRequest(
        @Schema(example = "Rua das Flores")
        @NotBlank String street,
        @Schema(example = "100")
        @NotBlank String number,
        @Schema(example = "São Paulo")
        @NotBlank String city,
        @Schema(example = "01310100")
        @NotBlank String zipCode
) {
}
