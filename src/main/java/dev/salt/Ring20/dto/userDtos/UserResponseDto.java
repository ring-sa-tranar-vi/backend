package dev.salt.Ring20.dto.userDtos;

public record UserResponseDto(
        Long id,
        String name,
        int intensityLevel,
        String context,
        boolean isAdmin,
        Long trainerId,
        String city,
        boolean onboarding) {}
