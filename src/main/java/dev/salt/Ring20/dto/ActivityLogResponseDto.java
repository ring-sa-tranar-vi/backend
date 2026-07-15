package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record ActivityLogResponseDto(
        Long id,
        Long userId,
        Long workoutId,
        LocalDateTime completedAt,
        Integer durationSeconds,
        String feedback,
        String status) {}
