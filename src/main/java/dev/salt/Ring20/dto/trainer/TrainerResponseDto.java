package dev.salt.Ring20.dto.trainer;

public record TrainerResponseDto(
        Long id,
        String name,
        String prompt,
        String voice,
        String intro,
        String language,
        String imageSelect,
        String imageCall,
        String imageStart,
        String ambience) {}
