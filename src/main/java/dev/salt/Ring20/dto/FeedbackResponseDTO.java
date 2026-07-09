package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.FeedbackDifficulty;
import java.time.LocalDateTime;

public record FeedbackResponseDTO(
        Long id,
        Long userId,
        Long workoutId,
        Long activityLogId,
        FeedbackDifficulty difficulty,
        Boolean liked,
        Integer rating,
        String comment,
        LocalDateTime createdAt) {}
