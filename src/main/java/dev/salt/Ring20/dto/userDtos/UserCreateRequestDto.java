package dev.salt.Ring20.dto.userDtos;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequestDto(@NotBlank String displayName) {}
