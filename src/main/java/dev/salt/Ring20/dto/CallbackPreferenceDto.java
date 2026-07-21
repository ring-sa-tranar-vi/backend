package dev.salt.Ring20.dto;

import java.time.LocalTime;

public record CallbackPreferenceDto(Long id, String day, LocalTime time, String repeatType) {}
