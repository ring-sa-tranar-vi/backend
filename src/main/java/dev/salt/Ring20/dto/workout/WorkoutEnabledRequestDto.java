package dev.salt.Ring20.dto.workout;

import jakarta.validation.constraints.NotNull;

public record WorkoutEnabledRequestDto(@NotNull Boolean enabled) {}
