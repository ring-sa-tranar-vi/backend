package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;

public record TrainerRequestDto(
        @NotBlank String name,
        String prompt,
        String voice,
        String intro,
        String language,
        String imageSelect,
        String imageCall,
        String imageStart,
        String ambience) {}
