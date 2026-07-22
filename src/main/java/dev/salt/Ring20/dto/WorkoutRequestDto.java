package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkoutRequestDto(
        @NotBlank String name,
        String description,
        String dashboardName,
        String dashboardDescription,
        String subtitleText,
        String instructionsSubtitleText,
        Integer level,
        String type,
        @NotNull Integer durationSeconds,
        String instructionsAudio,
        String workoutAudio,
        String instructionsImage,
        String workoutImage,
        String instructionsVideo,
        Integer instructionsVideoStart,
        Integer instructionsVideoStop,
        Boolean kneeFriendly,
        Boolean lowImpact,
        Boolean seated,
        Boolean beginnerFriendly,
        @NotNull TrainerIdDTO trainer) {
    public record TrainerIdDTO(Long id) {
    }
}
