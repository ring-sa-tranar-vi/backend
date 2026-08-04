package dev.salt.Ring20.dto.workoutDtos;

public record WorkoutResponseDto(
        Long id,
        String name,
        String description,
        String dashboardName,
        String dashboardDescription,
        String instructions,
        String guidance,
        Integer level,
        String type,
        String image,
        String video,
        Boolean enabled) {}
