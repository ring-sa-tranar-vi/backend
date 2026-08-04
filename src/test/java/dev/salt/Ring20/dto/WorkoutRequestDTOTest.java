package dev.salt.Ring20.dto;

import static org.junit.jupiter.api.Assertions.*;

import dev.salt.Ring20.dto.workoutDtos.WorkoutRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("WorkoutRequestDTO Tests")
class WorkoutRequestDTOTest {

    @Test
    void recordStoresValues() {
        WorkoutRequestDto dto =
                new WorkoutRequestDto(
                        "Push Ups",
                        "Upper body",
                        "Push Ups",
                        "Upper body dashboard",
                        "Do a push up.",
                        "Guide the user through the push up.",
                        1,
                        "strength",
                        "workout.png",
                        "workout.mp4");

        assertEquals("Push Ups", dto.name());
        assertEquals("Push Ups", dto.dashboardName());
        assertEquals("Do a push up.", dto.instructions());
        assertEquals("Guide the user through the push up.", dto.guidance());
        assertEquals("workout.png", dto.image());
        assertEquals("workout.mp4", dto.video());
    }
}
