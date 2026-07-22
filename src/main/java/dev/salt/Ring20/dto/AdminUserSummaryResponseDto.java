package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.UserRole;
import java.time.LocalDateTime;

public record AdminUserSummaryResponseDto(
        Long id,
        String name,
        String clerkId,
        UserRole role,
        Integer intensityLevel,
        Long trainerId,
        LocalDateTime lastCompletedWorkoutAt) {}
