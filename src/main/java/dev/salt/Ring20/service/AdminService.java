package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.AdminRecentActivityDTO;
import dev.salt.Ring20.dto.AdminTrainerOverviewDTO;
import dev.salt.Ring20.dto.AdminUserSummaryDTO;
import dev.salt.Ring20.dto.AdminWorkoutUsageDTO;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final WorkoutRepository workoutRepository;
    private final TrainerRepository trainerRepository;
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final int RECENT_ACTIVITY_LIMIT = 25;
    private static final String UNKNOWN_USER = "Unknown user";
    private static final String UNKNOWN_WORKOUT = "Unknown workout";

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

    public List<AdminUserSummaryDTO> getUserSummaries() {
        List<User> users = userRepository.findAll();
        Map<Long, LocalDateTime> lastCompletedAtByUserId =
                activityLogRepository.findByStatus(STATUS_COMPLETED).stream()
                        .filter(log -> log.getUserId() != null && log.getCompletedAt() != null)
                        .collect(Collectors.toMap(
                                ActivityLog::getUserId,
                                ActivityLog::getCompletedAt,
                                (existingTime, newTime) ->
                                        newTime.isAfter(existingTime) ? newTime : existingTime
                        ));

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .map(
                        user ->
                                new AdminUserSummaryDTO(
                                        user.getId(),
                                        user.getName(),
                                        user.getClerkId(),
                                        user.getRole(),
                                        user.getIntensityLevel(),
                                        user.getTrainerId(),
                                        lastCompletedAtByUserId.get(user.getId())))
                .toList();
    }

    public List<AdminRecentActivityDTO> getRecentActivityLogs() {
        Map<Long, String> userNameById = userRepository.findAll().stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getName
                ));

        Map<Long, String> workoutNameById = workoutRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Workout::getId,
                        Workout::getName
                ));

        return activityLogRepository.findAll().stream()
                .sorted(
                        Comparator.comparing(
                                        ActivityLog::getCompletedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(ActivityLog::getId, Comparator.reverseOrder()))
                .limit(RECENT_ACTIVITY_LIMIT)
                .map(
                        activityLog ->
                                new AdminRecentActivityDTO(
                                        activityLog.getId(),
                                        activityLog.getUserId(),
                                        userNameById.getOrDefault(
                                                activityLog.getUserId(), UNKNOWN_USER),
                                        activityLog.getWorkoutId(),
                                        workoutNameById.getOrDefault(
                                                activityLog.getWorkoutId(), UNKNOWN_WORKOUT),
                                        activityLog.getStatus(),
                                        activityLog.getDurationSeconds(),
                                        activityLog.getCompletedAt()))
                .toList();
    }

    public List<AdminWorkoutUsageDTO> getWorkoutUsage() {
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

        return workoutRepository.findAll().stream()
                .sorted(Comparator.comparing(Workout::getId))
                .map(
                        workout ->
                                new AdminWorkoutUsageDTO(
                                        workout.getId(),
                                        workout.getName(),
                                        workout.getTrainer() == null
                                                ? null
                                                : workout.getTrainer().getName(),
                                        startedCountByWorkoutId.getOrDefault(workout.getId(), 0L),
                                        completedCountByWorkoutId.getOrDefault(workout.getId(), 0L),
                                        lastCompletedAtByWorkoutId.get(workout.getId())))
                .toList();
    }

    public List<AdminTrainerOverviewDTO> getTrainerOverview() {
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

        return trainerRepository.findAll().stream()
                .sorted(Comparator.comparing(Trainer::getId))
                .map(
                        trainer ->
                                new AdminTrainerOverviewDTO(
                                        trainer.getId(),
                                        trainer.getName(),
                                        trainer.getLanguage(),
                                        assignedUserCountByTrainerId.getOrDefault(
                                                trainer.getId(), 0L),
                                        workoutCountByTrainerId.getOrDefault(trainer.getId(), 0L),
                                        enabledWorkoutCountByTrainerId.getOrDefault(
                                                trainer.getId(), 0L)))
                .toList();
    }

    public User updateUser(Long id, User updateData) {
        User existing =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND,
                                                "User not found"));

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
        if (updateData.getRole() != null && !updateData.getRole().isBlank()) {
            existing.setRole(updateData.getRole());
        }

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }

}
