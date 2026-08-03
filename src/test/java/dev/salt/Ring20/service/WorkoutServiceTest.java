package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutService Tests")
class WorkoutServiceTest {

    @Mock private WorkoutRepository workoutRepository;

    @Mock private ActivityLogRepository activityLogRepository;

    @Mock private FileStorageService fileStorageService;

    @InjectMocks private WorkoutService workoutService;

    private Workout workout;

    @BeforeEach
    void setUp() {
        workout = new Workout();
        workout.setId(1L);
        workout.setName("Push Ups");
    }

    @Test
    void getAllWorkoutsReturnsRepositoryResults() {
        when(workoutRepository.findAll()).thenReturn(List.of(workout));

        assertEquals(1, workoutService.getAllWorkouts(true).size());
    }

    @Test
    void startWorkoutCreatesActivityLogWhenUserPresent() {
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));
        when(activityLogRepository.save(any(ActivityLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        workoutService.startWorkout(1L, 55L);

        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(captor.capture());
        assertEquals(55L, captor.getValue().getUserId());
        assertEquals(1L, captor.getValue().getWorkoutId());
        assertEquals("STARTED", captor.getValue().getStatus());
    }

    @Test
    void createWorkoutRejectsBlankName() {
        workout.setName(" ");

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> workoutService.createWorkout(workout));
        assertEquals("Workout name is required.", ex.getMessage());
    }

    @Test
    void updateWorkoutRejectsInvalidId() {
        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> workoutService.updateWorkout(0L, workout));
        assertEquals("Id must be a positive number.", ex.getMessage());
    }

    @Test
    void deleteWorkoutDeletesExistingWorkout() {
        when(workoutRepository.existsById(1L)).thenReturn(true);

        workoutService.deleteWorkout(1L);

        verify(workoutRepository).deleteById(1L);
    }
}
