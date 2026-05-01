package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Endereço do usuário")
public record AddressResponse(
        @Schema(example = "Rua das Flores") String street,
        @Schema(example = "100") String number,
        @Schema(example = "São Paulo") String city,
        @Schema(example = "01310100") String zipCode
) {
}
