package dev.salt.Ring20.service.data;

import dev.salt.Ring20.entity.Workout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record WorkoutUsageData(
        List<Workout> workouts,
        Map<Long, Long> startedCountByWorkoutId,
        Map<Long, Long> completedCountByWorkoutId,
        Map<Long, LocalDateTime> lastCompletedAtByWorkoutId
) {
}
