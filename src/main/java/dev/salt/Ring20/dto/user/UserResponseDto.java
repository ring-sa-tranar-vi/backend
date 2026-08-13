package dev.salt.Ring20.dto.user;

public record UserResponseDto(
        Long id,
        String name,
        int intensityLevel,
        String context,
        boolean isAdmin,
        Long trainerId,
        String city,
        boolean onboarding,
        String timeZone) {}
