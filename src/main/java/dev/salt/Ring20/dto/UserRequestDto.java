package dev.salt.Ring20.dto;

public record UserRequestDto(
        String name, int intensityLevel, String context, Long trainerId, String city) {}
