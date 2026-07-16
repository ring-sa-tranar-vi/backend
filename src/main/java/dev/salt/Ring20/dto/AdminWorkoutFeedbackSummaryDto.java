package dev.salt.Ring20.dto;

public record AdminWorkoutFeedbackSummaryDto(
        Long workoutId,
        String workoutName,
        int feedbackCount,
        double avgRating,
        double dislikeRate,
        double tooHardRate,
        String status) {}
