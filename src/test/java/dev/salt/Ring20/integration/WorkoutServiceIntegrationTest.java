package dev.salt.Ring20.integration;

import static org.junit.jupiter.api.Assertions.*;

import dev.salt.Ring20.dto.WorkoutResponseDto;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.FileStorageService;
import dev.salt.Ring20.service.WorkoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@Import(WorkoutService.class)
@DisplayName("WorkoutService Integration Tests")
class WorkoutServiceIntegrationTest {

    @Autowired private WorkoutService workoutService;

    @Autowired private WorkoutRepository workoutRepository;

    @Autowired private ActivityLogRepository activityLogRepository;

    @MockitoBean private FileStorageService fileStorageService;

    @Test
    void createWorkoutPersistsWorkout() {
        Workout workout = new Workout();
        workout.setName("Integration Workout");
        workout.setDurationSeconds(180);

        WorkoutResponseDto created = workoutService.createWorkout(workout);

        assertNotNull(created.id());
        assertTrue(workoutRepository.findById(created.id()).isPresent());
    }

    @Test
    void startWorkoutReturnsExistingWorkout() {
        Workout workout = new Workout();
        workout.setName("Start Flow");
        workout.setDurationSeconds(90);

        Workout saved = workoutRepository.save(workout);

        WorkoutResponseDto started = workoutService.startWorkout(saved.getId(), null);

        assertNotNull(started);
        assertEquals(saved.getId(), started.id());
        assertEquals(0, activityLogRepository.findAll().size());
    }
}
