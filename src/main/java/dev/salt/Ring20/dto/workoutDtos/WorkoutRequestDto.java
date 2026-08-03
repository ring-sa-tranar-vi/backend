package dev.salt.Ring20.dto.workoutDtos;

import jakarta.validation.constraints.NotBlank;

public record WorkoutRequestDto(
        @NotBlank String name,
        String description,
        String dashboardName,
        String dashboardDescription,
        String instructions,
        String guidance,
        Integer level,
        String type,
        String image,
        String video) {}
