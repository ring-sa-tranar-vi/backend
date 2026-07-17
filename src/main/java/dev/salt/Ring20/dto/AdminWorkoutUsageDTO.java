package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record AdminWorkoutUsageDTO(
        Long workoutId,
        String workoutName,
        String trainerName,
        long startedCount,
        long completedCount,
        LocalDateTime lastCompletedAt) {}

