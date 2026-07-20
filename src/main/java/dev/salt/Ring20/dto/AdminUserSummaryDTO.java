package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record AdminUserSummaryDTO(
        Long id,
        String name,
        String clerkId,
        String role,
        Integer intensityLevel,
        Long trainerId,
        LocalDateTime lastCompletedWorkoutAt) {}
