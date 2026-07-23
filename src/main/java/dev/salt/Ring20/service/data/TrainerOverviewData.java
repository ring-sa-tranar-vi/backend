package dev.salt.Ring20.service.data;

import dev.salt.Ring20.entity.Trainer;
import java.util.List;
import java.util.Map;

public record TrainerOverviewData(
        List<Trainer> trainers,
        Map<Long, Long> assignedUserCountByTrainerId,
        Map<Long, Long> workoutCountByTrainerId,
        Map<Long, Long> enabledWorkoutCountByTrainerId) {}
