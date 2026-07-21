package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.FeedbackRequestDto;
import dev.salt.Ring20.dto.FeedbackResponseDto;
import dev.salt.Ring20.entity.Feedback;
import dev.salt.Ring20.service.FeedbackService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }


    @GetMapping
    public ResponseEntity<List<FeedbackResponseDto>> getFeedback(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long workoutId) {
        List<Feedback> feedbacks;

        if (userId != null && workoutId != null) {
            feedbacks = feedbackService.getFeedbackByUserAndWorkout(userId, workoutId);
        } else if (userId != null) {
            feedbacks = feedbackService.getFeedbackByUserId(userId);
        } else if (workoutId != null) {
            feedbacks = feedbackService.getFeedbackByWorkoutId(workoutId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(feedbacks.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getFeedbackById(id);

        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toResponse(feedback));
    }

    @PostMapping
    public ResponseEntity<FeedbackResponseDto> createFeedback(
            @RequestBody FeedbackRequestDto feedbackRequest) {
        Feedback saved = feedbackService.addFeedback(toEntity(feedbackRequest));
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        if (feedbackService.getFeedbackById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    private Feedback toEntity(FeedbackRequestDto request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(request.userId());
        feedback.setWorkoutId(request.workoutId());
        feedback.setActivityLogId(request.activityLogId());
        feedback.setDifficulty(request.difficulty());
        feedback.setLiked(request.liked());
        feedback.setRating(request.rating());
        feedback.setComment(request.comment());
        return feedback;
    }

    private FeedbackResponseDto toResponse(Feedback feedback) {
        return new FeedbackResponseDto(
                feedback.getId(),
                feedback.getUserId(),
                feedback.getWorkoutId(),
                feedback.getActivityLogId(),
                feedback.getDifficulty(),
                feedback.getLiked(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getCreatedAt());
    }
}
