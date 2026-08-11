package dev.salt.Ring20.dto.callback;

import java.time.LocalTime;

public record CallbackPreferenceResponseDto(
        Long id, String day, LocalTime time, String repeatType) {}
