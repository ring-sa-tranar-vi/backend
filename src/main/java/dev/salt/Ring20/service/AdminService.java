package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import dev.salt.Ring20.service.data.RecentActivityData;
import dev.salt.Ring20.service.data.TrainerOverviewData;
import dev.salt.Ring20.service.data.UserSummaryData;
import dev.salt.Ring20.service.data.WorkoutUsageData;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private static final String STATUS_COMPLETED = "COMPLETED";

    private static final int RECENT_ACTIVITY_LIMIT = 25;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final WorkoutRepository workoutRepository;
    private final TrainerRepository trainerRepository;

    public AdminService(
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository,
            WorkoutRepository workoutRepository,
            TrainerRepository trainerRepository,
            OrganisationRepository organisationRepository,
            EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.workoutRepository = workoutRepository;
        this.trainerRepository = trainerRepository;
    }

    public UserSummaryData getUserSummaries() {
        List<User> users = userRepository.findAll();
        Map<Long, LocalDateTime> lastCompletedAtByUserId =
                activityLogRepository.findByStatus(STATUS_COMPLETED).stream()
                        .filter(log -> log.getUserId() != null && log.getCompletedAt() != null)
                        .collect(
                                Collectors.toMap(
                                        ActivityLog::getUserId,
                                        ActivityLog::getCompletedAt,
                                        (existingTime, newTime) ->
                                                newTime.isAfter(existingTime)
                                                        ? newTime
                                                        : existingTime));

        return new UserSummaryData(users, lastCompletedAtByUserId);
    }

    public RecentActivityData getRecentActivityLogs() {
        Map<Long, String> userNameById =
                userRepository.findAll().stream()
                        .collect(Collectors.toMap(User::getId, User::getName));

        Map<Long, String> workoutNameById =
                workoutRepository.findAll().stream()
                        .collect(Collectors.toMap(Workout::getId, Workout::getName));

        List<ActivityLog> activityLogs = activityLogRepository.findAll().stream()
                .sorted(
                        Comparator.comparing(
                                        ActivityLog::getCompletedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(ActivityLog::getId, Comparator.reverseOrder()))
                .limit(RECENT_ACTIVITY_LIMIT)
                .toList();
        return new RecentActivityData(activityLogs, userNameById, workoutNameById);
    }

    public WorkoutUsageData getWorkoutUsage() {
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        Map<Long, Long> startedCountByWorkoutId = new HashMap<>();
        Map<Long, Long> completedCountByWorkoutId = new HashMap<>();
        Map<Long, LocalDateTime> lastCompletedAtByWorkoutId = new HashMap<>();

        for (ActivityLog activityLog : activityLogs) {
            Long workoutId = activityLog.getWorkoutId();
            if (workoutId == null) {
                continue;
            }

            startedCountByWorkoutId.merge(workoutId, 1L, Long::sum);

            if (STATUS_COMPLETED.equalsIgnoreCase(activityLog.getStatus())) {
                completedCountByWorkoutId.merge(workoutId, 1L, Long::sum);
                if (activityLog.getCompletedAt() != null) {
                    lastCompletedAtByWorkoutId.merge(
                            workoutId,
                            activityLog.getCompletedAt(),
                            (current, candidate) ->
                                    candidate.isAfter(current) ? candidate : current);
                }
            }
        }

        List<Workout> workouts = workoutRepository.findAll().stream()
                .sorted(Comparator.comparing(Workout::getId))
                .toList();
        return new WorkoutUsageData(
                workouts,
                startedCountByWorkoutId,
                completedCountByWorkoutId,
                lastCompletedAtByWorkoutId
        );
    }

    public TrainerOverviewData getTrainerOverview() {
        Map<Long, Long> assignedUserCountByTrainerId = new HashMap<>();
        for (User user : userRepository.findAll()) {
            if (user.getTrainerId() != null) {
                assignedUserCountByTrainerId.merge(user.getTrainerId(), 1L, Long::sum);
            }
        }

        Map<Long, Long> workoutCountByTrainerId = new HashMap<>();
        Map<Long, Long> enabledWorkoutCountByTrainerId = new HashMap<>();
        for (Workout workout : workoutRepository.findAll()) {
            if (workout.getTrainer() == null || workout.getTrainer().getId() == null) {
                continue;
            }

            Long trainerId = workout.getTrainer().getId();
            workoutCountByTrainerId.merge(trainerId, 1L, Long::sum);
            if (Boolean.TRUE.equals(workout.getEnabled())) {
                enabledWorkoutCountByTrainerId.merge(trainerId, 1L, Long::sum);
            }
        }

        List<Trainer> trainers = trainerRepository.findAll().stream()
                .sorted(Comparator.comparing(Trainer::getId))
                .toList();
        return new TrainerOverviewData(trainers, assignedUserCountByTrainerId, workoutCountByTrainerId, enabledWorkoutCountByTrainerId);
    }

    @Transactional
    public User updateUser(Long id, User updateData) {
        User existing =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NoSuchElementException("User not found with id: " + id));

        if (updateData.getName() != null && !updateData.getName().isBlank()) {
            existing.setName(updateData.getName());
        }
        if (updateData.getIntensityLevel() != null) {
            existing.setIntensityLevel(updateData.getIntensityLevel());
        }
        if (updateData.getContext() != null) {
            existing.setContext(updateData.getContext());
        }
        if (updateData.getTrainerId() != null) {
            existing.setTrainerId(updateData.getTrainerId());
        }
        if (updateData.getRole() != null) {
            existing.setRole(updateData.getRole());
        }

        return userRepository.save(existing);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NoSuchElementException("User not found with id: " + id));
        userRepository.delete(user);
    }
}
