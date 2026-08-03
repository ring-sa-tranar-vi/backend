package dev.salt.Ring20.dto.trainerDtos;

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
