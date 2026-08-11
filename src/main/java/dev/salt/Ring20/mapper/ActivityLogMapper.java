package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.activityLog.ActivityLogCreateRequestDto;
import dev.salt.Ring20.dto.activityLog.ActivityLogResponseDto;
import dev.salt.Ring20.entity.ActivityLog;

public class ActivityLogMapper {
    public static ActivityLog toEntity(ActivityLogCreateRequestDto request) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setWorkoutId(request.workoutId());
        activityLog.setCompletedAt(request.completedAt());
        activityLog.setDurationSeconds(request.durationSeconds());
        activityLog.setFeedback(request.feedback());
        activityLog.setStatus(request.status());
        return activityLog;
    }

    public static ActivityLogResponseDto toResponse(ActivityLog activityLog) {
        return new ActivityLogResponseDto(
                activityLog.getId(),
                activityLog.getUserId(),
                activityLog.getWorkoutId(),
                activityLog.getCompletedAt(),
                activityLog.getDurationSeconds(),
                activityLog.getFeedback(),
                activityLog.getStatus());
    }
}
