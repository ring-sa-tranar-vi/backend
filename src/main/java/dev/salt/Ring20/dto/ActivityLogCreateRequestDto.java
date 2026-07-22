package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ActivityLogCreateRequestDto(
        @NotNull Long userId,
        @NotNull Long workoutId,
        @NotNull LocalDateTime completedAt,
        @NotNull Integer durationSeconds,
        @NotNull String feedback,
        @NotBlank String status) {}
