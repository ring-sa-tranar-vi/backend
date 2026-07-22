package dev.salt.Ring20.dto;

public record AdminTrainerOverviewResponseDto(
        Long trainerId,
        String trainerName,
        String language,
        long assignedUserCount,
        long workoutCount,
        long enabledWorkoutCount) {}
