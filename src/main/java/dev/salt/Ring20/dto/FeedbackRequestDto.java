package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.FeedbackDifficulty;

public record FeedbackRequestDto(
        Long userId,
        Long workoutId,
        Long activityLogId,
        FeedbackDifficulty difficulty,
        Boolean liked,
        Integer rating,
        String comment) {}
