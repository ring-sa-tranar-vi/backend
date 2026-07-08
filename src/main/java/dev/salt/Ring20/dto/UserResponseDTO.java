package dev.salt.Ring20.dto;

public record UserResponseDTO(
        Long id,
        String name,
        int intensityLevel,
        String context,
        boolean isAdmin,
        Long trainerId
) {
}