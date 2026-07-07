package dev.salt.Ring20.dto;

public record UserRequestDTO(
    String name,
    int intensityLevel,
    String context,
    Long trainerId
) {}