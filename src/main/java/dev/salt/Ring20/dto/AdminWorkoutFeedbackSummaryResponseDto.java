package dev.salt.Ring20.dto;

public record AdminWorkoutFeedbackSummaryResponseDto(
        Long workoutId,
        String workoutName,
        int feedbackCount,
        double avgRating,
        double dislikeRate,
        double tooHardRate,
        String status) {}
