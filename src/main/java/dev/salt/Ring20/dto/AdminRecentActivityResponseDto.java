package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record AdminRecentActivityResponseDto(
        Long id,
        Long userId,
        String userName,
        Long workoutId,
        String workoutName,
        String status,
        Integer durationSeconds,
        LocalDateTime completedAt) {}
