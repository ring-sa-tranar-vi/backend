package dev.salt.Ring20.dto.adminDtos;

import java.time.LocalDateTime;

public record AdminWorkoutUsageResponseDto(
        Long workoutId,
        String workoutName,
        long startedCount,
        long completedCount,
        LocalDateTime lastCompletedAt) {}
