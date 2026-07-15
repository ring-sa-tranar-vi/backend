package dev.salt.Ring20.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequestDto(
        @NotBlank
        String name,
        String description,
        @NotNull LocalDateTime time) {}
