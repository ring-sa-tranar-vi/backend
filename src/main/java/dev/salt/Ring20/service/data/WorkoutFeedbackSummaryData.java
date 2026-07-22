package dev.salt.Ring20.service.data;

import dev.salt.Ring20.entity.Workout;

public record WorkoutFeedbackSummaryData(
        Workout workout,
        int feedbackCount,
        double avgRating,
        double dislikeRate,
        double tooHardRate,
        String status
) {
}
