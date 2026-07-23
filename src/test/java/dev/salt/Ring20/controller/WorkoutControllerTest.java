package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.dto.WorkoutEnabledRequestDto;
import dev.salt.Ring20.dto.WorkoutRequestDto;
import dev.salt.Ring20.dto.WorkoutResponseDto;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.service.FileStorageService;
import dev.salt.Ring20.service.UserService;
import dev.salt.Ring20.service.WorkoutService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutController Tests")
class WorkoutControllerTest {

    @Mock private WorkoutService workoutService;

    @Mock private UserService userService;

    @Mock private FileStorageService fileStorageService;

    @InjectMocks private WorkoutController workoutController;

    @Test
    void getAllWorkoutsReturnsData() {
        Workout workout = new Workout();
        workout.setId(1L);
        workout.setName("Push Ups");
        workout.setEnabled(true);

        when(userService.isAdmin("admin_1")).thenReturn(true);
        when(workoutService.getAllWorkouts(true)).thenReturn(List.of(workout));

        ResponseEntity<List<WorkoutResponseDto>> response =
                workoutController.getAllWorkouts(auth("admin_1"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Push Ups", response.getBody().get(0).name());
    }

    @Test
    void createWorkoutReturnsForbiddenForNonAdmin() {
        WorkoutRequestDto request =
                new WorkoutRequestDto(
                        "Test Workout",
                        "desc",
                        null,
                        null,
                        null,
                        null,
                        1,
                        "strength",
                        300,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        true,
                        false,
                        false,
                        true,
                        null);

        when(userService.isAdmin("user_1")).thenReturn(false);

        ResponseEntity<WorkoutResponseDto> response =
                workoutController.createWorkout(request, auth("user_1"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(workoutService, never()).createWorkout(any());
    }

    @Test
    void createWorkoutReturnsOkForAdmin() {
        WorkoutRequestDto request =
                new WorkoutRequestDto(
                        "Test Workout",
                        "desc",
                        null,
                        null,
                        null,
                        null,
                        1,
                        "strength",
                        300,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        true,
                        false,
                        false,
                        true,
                        null);

        when(userService.isAdmin("admin_1")).thenReturn(true);

        Workout workout = new Workout();
        workout.setName("Test Workout");
        workout.setEnabled(true);

        when(workoutService.createWorkout(any())).thenReturn(workout);

        ResponseEntity<WorkoutResponseDto> response =
                workoutController.createWorkout(request, auth("admin_1"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Workout", response.getBody().name());
    }

    @Test
    void deleteWorkoutReturnsNoContentForAdmin() {
        when(userService.isAdmin("admin_1")).thenReturn(true);

        ResponseEntity<Void> response = workoutController.deleteWorkout(1L, auth("admin_1"));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(workoutService).deleteWorkout(1L);
    }

    @Test
    void setWorkoutEnabledReturnsForbiddenForNonAdmin() {
        when(userService.isAdmin("user_1")).thenReturn(false);

        ResponseEntity<WorkoutResponseDto> response =
                workoutController.setWorkoutEnabled(
                        1L, new WorkoutEnabledRequestDto(false), auth("user_1"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(workoutService, never()).setWorkoutEnabled(anyLong(), anyBoolean());
    }

    @Test
    void setWorkoutEnabledReturnsOkForAdmin() {
        when(userService.isAdmin("admin_1")).thenReturn(true);

        Workout updated = new Workout();
        updated.setId(1L);
        updated.setName("Push Ups");
        updated.setEnabled(false);

        when(workoutService.setWorkoutEnabled(1L, false)).thenReturn(updated);

        ResponseEntity<WorkoutResponseDto> response =
                workoutController.setWorkoutEnabled(
                        1L, new WorkoutEnabledRequestDto(false), auth("admin_1"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().enabled());
    }

    private Authentication auth(String subject) {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(subject);

        return auth;
    }
}
