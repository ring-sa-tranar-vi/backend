package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final WorkoutRepository workoutRepository;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository, WorkoutRepository workoutRepository) {
        this.activityLogRepository = activityLogRepository;
        this.workoutRepository = workoutRepository;
    }

    public ActivityLog createActivityLog(ActivityLog activityLog) {
        activityLog.setCompletedAt(LocalDateTime.now());
        return activityLogRepository.save(activityLog);
    }

    public ActivityLog completeActivityLog(Long id) {
        ActivityLog log =
                activityLogRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND,
                                                "ActivityLog not found"));
        log.setStatus("COMPLETED");
        log.setCompletedAt(LocalDateTime.now());
        return activityLogRepository.save(log);
    }

    public Map<String, Object> getUserProgress(Long userId) {
        List<ActivityLog> completedLogs =
                activityLogRepository.findByUserIdAndStatusOrderByCompletedAtDesc(
                        userId, "COMPLETED");

        Map<Long, String> workoutNameById = new HashMap<>();
        Map<LocalDate, LinkedHashSet<String>> workoutsByDate = new LinkedHashMap<>();
        DateTimeFormatter labelFormatter =
                DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ENGLISH);

        for (ActivityLog log : completedLogs) {
            if (log.getCompletedAt() == null) {
                continue;
            }

            Long workoutId = log.getWorkoutId();
            String workoutName = workoutNameById.get(workoutId);

            if (workoutName == null) {
                Optional<Workout> workout = workoutRepository.findById(workoutId);
                workoutName = workout.map(Workout::getName).orElse("Unknown workout");
                workoutNameById.put(workoutId, workoutName);
            }

            LocalDate completedDate = log.getCompletedAt().toLocalDate();
            workoutsByDate
                    .computeIfAbsent(completedDate, ignored -> new LinkedHashSet<>())
                    .add(workoutName);
        }

        List<Map<String, Object>> completedWorkouts = new ArrayList<>();
        for (Map.Entry<LocalDate, LinkedHashSet<String>> entry : workoutsByDate.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("dateLabel", entry.getKey().format(labelFormatter));
            item.put("workoutName", String.join(", ", entry.getValue()));
            completedWorkouts.add(item);
        }

        List<LocalDate> ascendingDates = new ArrayList<>(workoutsByDate.keySet());
        Collections.sort(ascendingDates);

        List<String> completedDates = ascendingDates.stream().map(LocalDate::toString).toList();

        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        int completedThisWeek =
                (int) ascendingDates.stream().filter(date -> !date.isBefore(startOfWeek)).count();

        int personalBestStreak = calculateBestStreak(ascendingDates);

        int currentStreak = calculateCurrentStreak(new ArrayList<>(workoutsByDate.keySet()));

        Map<String, Object> response = new HashMap<>();
        response.put("currentStreak", currentStreak);
        response.put("completedWorkouts", completedWorkouts);

        response.put("completedDates", completedDates);
        response.put("completedThisWeek", completedThisWeek);
        response.put("personalBestStreak", personalBestStreak);

        return response;
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
                userId, "COMPLETED", startOfDay, endOfDay);
    }

    public long getActiveUserCount() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return activityLogRepository.countDistinctActiveUsersSince(since);
    }
}
