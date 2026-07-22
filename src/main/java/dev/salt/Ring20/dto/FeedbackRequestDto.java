package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.FeedbackDifficulty;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequestDto(
        @NotNull Long userId,
        @NotNull Long workoutId,
        @NotNull Long activityLogId,
        FeedbackDifficulty difficulty,
        Boolean liked,
        Integer rating,
        String comment) {}
