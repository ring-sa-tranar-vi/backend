package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotNull;

public record FcmTokenRequestDto(@NotNull String token) {
}
