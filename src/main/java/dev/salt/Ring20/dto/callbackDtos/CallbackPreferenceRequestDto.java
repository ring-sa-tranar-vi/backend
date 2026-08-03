package dev.salt.Ring20.dto.callbackDtos;

import java.time.LocalTime;

public record CallbackPreferenceRequestDto(String day, LocalTime time, String repeatType) {}
