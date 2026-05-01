package com.restaurant.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Representação do usuário (sem senha)")
public record UserResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Maria Silva") String name,
        @Schema(example = "maria@email.com") String email,
        @Schema(example = "maria.silva") String login,
        AddressResponse address,
        @Schema(example = "CLIENT") UserType userType,
        @Schema(example = "2026-05-01T12:00:00Z") Instant lastModifiedAt
) {
}
