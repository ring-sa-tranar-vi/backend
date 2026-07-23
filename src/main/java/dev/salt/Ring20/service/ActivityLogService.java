package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private final ActivityLogRepository activityLogRepository;
    private final WorkoutRepository workoutRepository;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository, WorkoutRepository workoutRepository) {
        this.activityLogRepository = activityLogRepository;
        this.workoutRepository = workoutRepository;
    }

    @Transactional
    public ActivityLog createActivityLog(ActivityLog activityLog) {
        activityLog.setCompletedAt(LocalDateTime.now());
        return activityLogRepository.save(activityLog);
    }

    @Transactional
    public ActivityLog completeActivityLog(Long id) {
        ActivityLog log =
                activityLogRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "ActivityLog not found with id:" + id));
        log.setStatus(STATUS_COMPLETED);
        log.setCompletedAt(LocalDateTime.now());
        return activityLogRepository.save(log);
    }

    public Map<String, Object> getUserProgress(Long userId) {
        List<ActivityLog> completedLogs =
                activityLogRepository.findByUserIdAndStatusOrderByCompletedAtDesc(
                        userId, STATUS_COMPLETED);

        List<ActivityLog> validLogs =
                completedLogs.stream().filter(log -> log.getCompletedAt() != null).toList();

        Set<Long> workoutIds =
                validLogs.stream().map(ActivityLog::getWorkoutId).collect(Collectors.toSet());

        Map<Long, String> workoutNameById =
                workoutRepository.findAllById(workoutIds).stream()
                        .collect(
                                Collectors.toMap(
                                        Workout::getId,
                                        w ->
                                                Optional.ofNullable(w.getName())
                                                        .orElse("Unknown workout")));

        Map<LocalDate, LinkedHashSet<String>> workoutsByDate = new LinkedHashMap<>();

        for (ActivityLog log : validLogs) {
            LocalDate date = log.getCompletedAt().toLocalDate();
            String workoutName =
                    workoutNameById.getOrDefault(log.getWorkoutId(), "Unknown workout");

            workoutsByDate.computeIfAbsent(date, d -> new LinkedHashSet<>()).add(workoutName);
        }

        DateTimeFormatter labelFormatter =
                DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ENGLISH);

        List<Map<String, Object>> completedWorkouts =
                workoutsByDate.entrySet().stream()
                        .map(
                                entry -> {
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("dateLabel", entry.getKey().format(labelFormatter));
                                    item.put("workoutName", String.join(", ", entry.getValue()));
                                    return item;
                                })
                        .toList();

        int currentStreak = calculateCurrentStreak(new ArrayList<>(workoutsByDate.keySet()));

        return Map.of("currentStreak", currentStreak, "completedWorkouts", completedWorkouts);
    }

    private int calculateBestStreak(List<LocalDate> sortedDates) {
        if (sortedDates == null || sortedDates.isEmpty()) return 0;

        int currentStreak = 1;
        int maxStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            if (sortedDates.get(i - 1).plusDays(1).equals(sortedDates.get(i))) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }
            maxStreak = Math.max(maxStreak, currentStreak);
        }
        return maxStreak;
    }

    private int calculateCurrentStreak(List<LocalDate> dates) {
        if (dates.isEmpty()) {
            return 0;
        }

        int streak = 1;
        for (int i = 1; i < dates.size(); i++) {
            LocalDate previousDay = dates.get(i - 1).minusDays(1);
            if (dates.get(i).isEqual(previousDay)) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    public boolean hasCompletedWorkoutToday(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return activityLogRepository.existsByUserIdAndStatusAndCompletedAtBetween(
                userId, STATUS_COMPLETED, startOfDay, endOfDay);
    }

    public long getActiveUserCount() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return activityLogRepository.countDistinctActiveUsersSince(since);
    }
}
