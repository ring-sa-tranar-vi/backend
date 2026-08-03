package dev.salt.Ring20.dto.adminDtos;

import dev.salt.Ring20.entity.enums.FeedbackDifficulty;
import java.time.LocalDateTime;

public record AdminRecentFeedbackResponseDto(
        Long id,
        Long userId,
        Long workoutId,
        Long activityLogId,
        String workoutName,
        FeedbackDifficulty difficulty,
        Boolean liked,
        Integer rating,
        String comment,
        LocalDateTime createdAt) {}
