package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record ActivityLogResponseDTO(
        Long id,
        Long userId,
        Long workoutId,
        LocalDateTime completedAt,
        Integer durationSeconds,
        String feedback,
        String status) {}
