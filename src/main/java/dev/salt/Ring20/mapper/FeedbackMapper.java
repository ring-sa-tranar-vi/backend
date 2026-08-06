package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.feedbackDtos.FeedbackRequestDto;
import dev.salt.Ring20.dto.feedbackDtos.FeedbackResponseDto;
import dev.salt.Ring20.entity.Feedback;

public class FeedbackMapper {
    public static FeedbackResponseDto toResponse(Feedback feedback){
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

    public static Feedback toEntity(FeedbackRequestDto request) {
        Feedback feedback = new Feedback();
        feedback.setWorkoutId(request.workoutId());
        feedback.setActivityLogId(request.activityLogId());
        feedback.setDifficulty(request.difficulty());
        feedback.setLiked(request.liked());
        feedback.setRating(request.rating());
        feedback.setComment(request.comment());
        return feedback;
    }
}
