package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.dto.AdminWorkoutFeedbackSummaryDto;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Feedback;
import dev.salt.Ring20.entity.FeedbackDifficulty;
import dev.salt.Ring20.entity.UserWorkoutPreferenceType;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.FeedbackRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackService Tests")
class FeedbackServiceTest {

    @Mock private FeedbackRepository feedbackRepository;

    @Mock private WorkoutRepository workoutRepository;

    @Mock private UserWorkoutPreferenceService preferenceService;

    @Mock private ActivityLogRepository activityLogRepository;

    @InjectMocks private FeedbackService feedbackService;

    private Feedback feedback;
    private Workout workout;
    private ActivityLog activityLog;

    private void stubActivityLogLookup() {
        when(activityLogRepository.findTopByUserIdAndWorkoutIdAndStatusOrderByCompletedAtDesc(
                        anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(activityLog));
    }

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setUserId(2L);
        feedback.setWorkoutId(3L);
        feedback.setRating(4);
        feedback.setLiked(true);
        feedback.setDifficulty(FeedbackDifficulty.JUST_RIGHT);

        workout = new Workout();
        workout.setId(3L);
        workout.setName("Push Ups");

        activityLog = new ActivityLog();
        activityLog.setId(99L);
        activityLog.setUserId(2L);
        activityLog.setWorkoutId(3L);
    }

    @Test
    void addFeedbackStoresTimestampAndSaves() {
        stubActivityLogLookup();
        when(feedbackRepository.save(any(Feedback.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Feedback saved = feedbackService.addFeedback(feedback);

        assertNotNull(saved.getCreatedAt());
        verify(feedbackRepository).save(feedback);
    }

    @Test
    void addFeedbackAddsDislikedPreferenceWhenLikedIsFalse() {
        stubActivityLogLookup();
        feedback.setLiked(false);
        when(feedbackRepository.save(any(Feedback.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        feedbackService.addFeedback(feedback);

        verify(preferenceService).addPreference(2L, 3L, UserWorkoutPreferenceType.DISLIKED);
    }

    @Test
    void addFeedbackRejectsMissingUserOrWorkoutId() {
        feedback.setUserId(null);

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class, () -> feedbackService.addFeedback(feedback));
        assertEquals("userId and workoutId are required", ex.getReason());
    }

    @Test
    void addFeedbackRejectsRatingOutsideRange() {
        feedback.setRating(6);

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class, () -> feedbackService.addFeedback(feedback));
        assertEquals("rating must be between 1 and 5", ex.getReason());
    }

    @Test
    void getWorkoutFeedbackSummaryReturnsDataForWorkout() {
        Feedback other = new Feedback();
        other.setWorkoutId(3L);
        other.setRating(5);
        other.setLiked(true);
        other.setDifficulty(FeedbackDifficulty.JUST_RIGHT);

        when(workoutRepository.findAll()).thenReturn(List.of(workout));
        when(feedbackRepository.findAll()).thenReturn(List.of(feedback, other));

        List<AdminWorkoutFeedbackSummaryDto> summary = feedbackService.getWorkoutFeedbackSummary();

        assertEquals(1, summary.size());
        assertEquals(2, summary.get(0).feedbackCount());
    }
}
