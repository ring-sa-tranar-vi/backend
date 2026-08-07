package dev.salt.Ring20.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequestDto(@NotBlank String displayName) {}
