package dev.salt.Ring20.dto.activityLog;

import dev.salt.Ring20.entity.ActivityLog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ActivityLogCreateRequestDto(
        @NotNull Long workoutId,
        @NotNull LocalDateTime completedAt,
        @NotNull Integer durationSeconds,
        @NotNull String feedback,
        @NotBlank String status) {}
