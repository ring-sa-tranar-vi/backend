package dev.salt.Ring20.dto.admin;

public record AdminTrainerOverviewResponseDto(
        Long trainerId, String trainerName, String language, long assignedUserCount) {}
