package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.feedback.FeedbackRequestDto;
import dev.salt.Ring20.dto.feedback.FeedbackResponseDto;
import dev.salt.Ring20.entity.Feedback;
import dev.salt.Ring20.mapper.FeedbackMapper;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@Tag(
        name = "Feedback",
        description = "Endpoints for creating, retrieving, and managing workout feedback.")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final SecurityService securityService;

    public FeedbackController(FeedbackService feedbackService, SecurityService securityService) {
        this.feedbackService = feedbackService;
        this.securityService = securityService;
    }

    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get feedback",
            description = "Retrieves feedback entries filtered by user or workout.")
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long workoutId) {

        List<Feedback> feedbacks = feedbackService.getFeedback(userId, workoutId);

        return ResponseEntity.ok()
                .body(feedbacks.stream().map(FeedbackMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get feedback by ID",
            description = "Retrieves a feedback entry using its ID.")
    @PreAuthorize("@feedbackSecurity.canModify(#id, authentication.name)")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok().body(FeedbackMapper.toResponse(feedback));
    }

    @PostMapping
    @Operation(summary = "Create feedback", description = "Creates a new feedback entry.")
    public ResponseEntity<FeedbackResponseDto> createFeedback(
            @Valid @RequestBody FeedbackRequestDto feedbackRequest, Authentication authentication) {
        Feedback feedback = FeedbackMapper.toEntity(feedbackRequest);

        feedback.setUserId(securityService.currentUserId(authentication.getName()));

        Feedback saved = feedbackService.addFeedback(feedback);

        return ResponseEntity.status(HttpStatus.CREATED).body(FeedbackMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete feedback", description = "Deletes a feedback entry by its ID.")
    @PreAuthorize("@feedbackSecurity.canModify(#id, authentication.name)")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {

        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
