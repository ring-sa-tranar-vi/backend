package dev.salt.Ring20.dto.admin;

import dev.salt.Ring20.entity.enums.UserRole;
import java.time.LocalDateTime;

public record AdminUserSummaryResponseDto(
        Long id,
        String name,
        String clerkId,
        UserRole role,
        Integer intensityLevel,
        String context,
        Long trainerId,
        String city,
        boolean active,
        boolean enabled,
        LocalDateTime lastCompletedWorkoutAt) {}
