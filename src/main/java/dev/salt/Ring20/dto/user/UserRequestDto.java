package dev.salt.Ring20.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDto(
        @NotBlank String name,
        @Min(1) @Max(5) int intensityLevel,
        String context,
        @NotNull Long trainerId,
        String city,
        boolean onboarding) {}
