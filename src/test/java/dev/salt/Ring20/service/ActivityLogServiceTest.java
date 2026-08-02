package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityLogService Tests")
class ActivityLogServiceTest {

    @Mock private ActivityLogRepository activityLogRepository;

    @Mock private WorkoutRepository workoutRepository;

    @InjectMocks private ActivityLogService activityLogService;

    @Test
    void createActivityLogAddsTimestamp() {
        ActivityLog log = new ActivityLog();
        when(activityLogRepository.save(any(ActivityLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ActivityLog saved = activityLogService.createActivityLog(log);

        assertNotNull(saved.getCompletedAt());
    }

    @Test
    void completeActivityLogThrowsWhenMissing() {
        when(activityLogRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException ex =
                assertThrows(
                        NoSuchElementException.class,
                        () -> activityLogService.completeActivityLog(1L));
        assertEquals("ActivityLog not found with id:1", ex.getMessage());
    }

    @Test
    void getUserProgressReturnsZeroWhenNoLogsExist() {
        when(activityLogRepository.findByUserIdAndStatusOrderByCompletedAtDesc(1L, "COMPLETED"))
                .thenReturn(List.of());

        Map<String, Object> progress = activityLogService.getUserProgress(1L);

        assertEquals(0, progress.get("currentStreak"));
        assertEquals(0, progress.get("personalBestStreak"));
        assertEquals(List.of(), progress.get("completedDates"));
    }

    @Test
    void getUserProgressBuildsCompletedWorkouts() {
        ActivityLog log = new ActivityLog();
        log.setUserId(1L);
        log.setWorkoutId(7L);
        log.setStatus("COMPLETED");
        log.setCompletedAt(LocalDateTime.now());

        Workout workout = new Workout();
        workout.setId(7L);
        workout.setName("Squats");

        when(activityLogRepository.findByUserIdAndStatusOrderByCompletedAtDesc(1L, "COMPLETED"))
                .thenReturn(List.of(log));
        when(workoutRepository.findAllById(any())).thenReturn(List.of(workout));

        Map<String, Object> progress = activityLogService.getUserProgress(1L);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> completed =
                (List<Map<String, Object>>) progress.get("completedWorkouts");

        assertEquals(1, completed.size());
        assertEquals("Squats", completed.get(0).get("workoutName"));
    }

    @Test
    void getUserProgressReturnsCompletedDatesAndPersonalBestStreak() {
        LocalDateTime today = LocalDateTime.now();
        ActivityLog latest = completedLog(1L, today);
        ActivityLog yesterday = completedLog(2L, today.minusDays(1));
        ActivityLog older = completedLog(3L, today.minusDays(3));

        when(activityLogRepository.findByUserIdAndStatusOrderByCompletedAtDesc(1L, "COMPLETED"))
                .thenReturn(List.of(latest, yesterday, older));
        when(workoutRepository.findAllById(any())).thenReturn(List.of());

        Map<String, Object> progress = activityLogService.getUserProgress(1L);

        assertEquals(2, progress.get("currentStreak"));
        assertEquals(2, progress.get("personalBestStreak"));
        assertEquals(
                List.of(
                        today.minusDays(3).toLocalDate().toString(),
                        today.minusDays(1).toLocalDate().toString(),
                        today.toLocalDate().toString()),
                progress.get("completedDates"));
    }

    private ActivityLog completedLog(Long id, LocalDateTime completedAt) {
        ActivityLog log = new ActivityLog();
        log.setId(id);
        log.setUserId(1L);
        log.setWorkoutId(7L);
        log.setStatus("COMPLETED");
        log.setCompletedAt(completedAt);
        return log;
    }
}
