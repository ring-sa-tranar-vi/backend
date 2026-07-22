package dev.salt.Ring20.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Workout Entity Tests")
class WorkoutEntityTest {

    @Test
    void durationSecondsAllowsNegativeValuesAtEntityLevel() {
        Workout workout = new Workout();
        workout.setDurationSeconds(-1);

        assertEquals(-1, workout.getDurationSeconds());
    }

    @Test
    void durationSecondsAcceptsNullAndPositiveValues() {
        Workout workout = new Workout();
        workout.setDurationSeconds(null);
        workout.setDurationSeconds(120);

        assertEquals(120, workout.getDurationSeconds());
    }
}
