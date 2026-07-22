package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.dto.FeedbackRequestDto;
import dev.salt.Ring20.dto.FeedbackResponseDto;
import dev.salt.Ring20.entity.Feedback;
import dev.salt.Ring20.entity.FeedbackDifficulty;
import dev.salt.Ring20.service.FeedbackService;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackController Tests")
class FeedbackControllerTest {

    @Mock private FeedbackService feedbackService;

    @Test
    void createFeedbackReturnsSavedEntity() {
        FeedbackController controller = new FeedbackController(feedbackService);
        FeedbackRequestDto request =
                new FeedbackRequestDto(
                        1L, 2L, null, FeedbackDifficulty.JUST_RIGHT, true, 4, "Great");
        Feedback feedback = new Feedback();
        feedback.setId(9L);
        feedback.setUserId(1L);
        feedback.setWorkoutId(2L);
        feedback.setDifficulty(FeedbackDifficulty.JUST_RIGHT);
        when(feedbackService.addFeedback(any(Feedback.class))).thenReturn(feedback);

        ResponseEntity<FeedbackResponseDto> response = controller.createFeedback(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(9L, response.getBody().id());
    }

    @Test
    void getFeedbackByIdThrowsWhenMissing() {
        FeedbackController controller = new FeedbackController(feedbackService);
        when(feedbackService.getFeedbackById(1L))
                .thenThrow(new NoSuchElementException("Feedback not found with id: 1"));

        assertThrows(
                NoSuchElementException.class, () -> controller.getFeedbackById(1L));
    }

    @Test
    void deleteFeedbackReturnsNoContentWhenPresent() {
        FeedbackController controller = new FeedbackController(feedbackService);
        when(feedbackService.getFeedbackById(1L)).thenReturn(new Feedback());

        ResponseEntity<Void> response = controller.deleteFeedback(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(feedbackService).deleteFeedback(1L);
    }

    @Test
    void getFeedbackThrowsWithoutFilters() {
        FeedbackController controller = new FeedbackController(feedbackService);
        when(feedbackService.getFeedback(null, null))
                .thenThrow(new IllegalArgumentException("At least one filter must be provided"));

        assertThrows(
                IllegalArgumentException.class, () -> controller.getFeedback(null, null));
    }
}
