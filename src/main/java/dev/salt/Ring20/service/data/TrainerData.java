package dev.salt.Ring20.service.data;

public record TrainerData(
        String name,
        String prompt,
        String voice,
        String intro,
        String language,
        String imageSelect,
        String imageCall,
        String imageStart,
        String ambience
) {
}
