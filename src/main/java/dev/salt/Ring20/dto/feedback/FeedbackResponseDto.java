package dev.salt.Ring20.dto.feedback;

import dev.salt.Ring20.entity.enums.FeedbackDifficulty;
import java.time.LocalDateTime;

public record FeedbackResponseDto(
        Long id,
        Long userId,
        Long workoutId,
        Long activityLogId,
        FeedbackDifficulty difficulty,
        Boolean liked,
        Integer rating,
        String comment,
        LocalDateTime createdAt) {}
