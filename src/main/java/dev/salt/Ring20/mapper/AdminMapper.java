package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.adminDtos.*;
import dev.salt.Ring20.service.data.*;
import java.util.List;

public class AdminMapper {
    private static final int ACTIVE_USER_DAYS = 30;
    private static final String UNKNOWN_USER = "Unknown user";
    private static final String UNKNOWN_WORKOUT = "Unknown workout";

    public static List<AdminRecentFeedbackResponseDto> toAdminRecentFeedbackResponseDto(
            RecentFeedbackData data) {

        return data.feedbacks().stream()
                .map(
                        feedback ->
                                new AdminRecentFeedbackResponseDto(
                                        feedback.getId(),
                                        feedback.getUserId(),
                                        feedback.getWorkoutId(),
                                        feedback.getActivityLogId(),
                                        data.workoutNameById()
                                                .getOrDefault(
                                                        feedback.getWorkoutId(), UNKNOWN_WORKOUT),
                                        feedback.getDifficulty(),
                                        feedback.getLiked(),
                                        feedback.getRating(),
                                        feedback.getComment(),
                                        feedback.getCreatedAt()))
                .toList();
    }

    public static List<AdminTrainerOverviewResponseDto> toAdminTrainerOverviewsResponseDto(
            TrainerOverviewData data) {
        return data.trainers().stream()
                .map(
                        trainer ->
                                new AdminTrainerOverviewResponseDto(
                                        trainer.getId(),
                                        trainer.getName(),
                                        trainer.getLanguage(),
                                        data.assignedUserCountByTrainerId()
                                                .getOrDefault(trainer.getId(), 0L)))
                .toList();
    }

    public static List<AdminWorkoutUsageResponseDto> toAdminWorkoutUsageResponseDto(
            WorkoutUsageData data) {
        return data.workouts().stream()
                .map(
                        workout ->
                                new AdminWorkoutUsageResponseDto(
                                        workout.getId(),
                                        workout.getName(),
                                        data.startedCountByWorkoutId()
                                                .getOrDefault(workout.getId(), 0L),
                                        data.completedCountByWorkoutId()
                                                .getOrDefault(workout.getId(), 0L),
                                        data.lastCompletedAtByWorkoutId().get(workout.getId())))
                .toList();
    }

    public static List<AdminRecentActivityResponseDto> toAdminRecentActivityResponseDto(
            RecentActivityData data) {
        return data.activityLogs().stream()
                .map(
                        activityLog ->
                                new AdminRecentActivityResponseDto(
                                        activityLog.getId(),
                                        activityLog.getUserId(),
                                        data.userNameById()
                                                .getOrDefault(
                                                        activityLog.getUserId(), UNKNOWN_USER),
                                        activityLog.getWorkoutId(),
                                        data.workoutNameById()
                                                .getOrDefault(
                                                        activityLog.getWorkoutId(),
                                                        UNKNOWN_WORKOUT),
                                        activityLog.getStatus(),
                                        activityLog.getDurationSeconds(),
                                        activityLog.getCompletedAt()))
                .toList();
    }

    public static List<AdminUserSummaryResponseDto> toAdminUserSummaryResponseDto(
            UserSummaryData data) {
        return data.users().stream()
                .map(
                        user ->
                                new AdminUserSummaryResponseDto(
                                        user.getId(),
                                        user.getName(),
                                        user.getClerkId(),
                                        user.getRole(),
                                        user.getIntensityLevel(),
                                        user.getContext(),
                                        user.getTrainerId(),
                                        user.getCity(),
                                        isActive(data.lastCompletedAtByUserId().get(user.getId())),
                                        true,
                                        data.lastCompletedAtByUserId().get(user.getId())))
                .toList();
    }

    public static List<AdminWorkoutFeedbackSummaryResponseDto> toWorkoutFeedbackSummaryDto(
            List<WorkoutFeedbackSummaryData> data) {
        return data.stream()
                .map(
                        summary ->
                                new AdminWorkoutFeedbackSummaryResponseDto(
                                        summary.workout().getId(),
                                        summary.workout().getName(),
                                        summary.feedbackCount(),
                                        summary.avgRating(),
                                        summary.dislikeRate(),
                                        summary.tooHardRate(),
                                        summary.status()))
                .toList();
    }

    private static boolean isActive(java.time.LocalDateTime lastCompletedAt) {
        return lastCompletedAt != null
                && !lastCompletedAt.isBefore(
                        java.time.LocalDateTime.now().minusDays(ACTIVE_USER_DAYS));
    }
}
