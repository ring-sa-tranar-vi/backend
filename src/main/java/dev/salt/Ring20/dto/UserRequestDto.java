package dev.salt.Ring20.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDto(
        @NotBlank String name,
        @Min(0) @Max(4) int intensityLevel,
        String context,
        @NotNull Long trainerId,
        String city) {
}
