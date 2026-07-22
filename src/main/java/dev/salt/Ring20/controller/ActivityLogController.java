package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.ActivityLogCreateRequestDto;
import dev.salt.Ring20.dto.ActivityLogResponseDto;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.service.ActivityLogService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/users/{userId}/has-completed-today")
    public ResponseEntity<Map<String, Boolean>> hasCompletedWorkoutToday(
            @PathVariable Long userId) {
        boolean hasCompleted = activityLogService.hasCompletedWorkoutToday(userId);
        return ResponseEntity.ok(Map.of("hasCompletedToday", hasCompleted));
    }

    @PostMapping
    public ResponseEntity<ActivityLogResponseDto> createActivityLog(
            @Valid @RequestBody ActivityLogCreateRequestDto activityLogRequest) {
        ActivityLog created = activityLogService.createActivityLog(toEntity(activityLogRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ActivityLogResponseDto> completeActivityLog(@PathVariable Long id) {
        ActivityLog completed = activityLogService.completeActivityLog(id);
        return ResponseEntity.ok().body(toResponse(completed));
    }

    private ActivityLog toEntity(ActivityLogCreateRequestDto request) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserId(request.userId());
        activityLog.setWorkoutId(request.workoutId());
        activityLog.setCompletedAt(request.completedAt());
        activityLog.setDurationSeconds(request.durationSeconds());
        activityLog.setFeedback(request.feedback());
        activityLog.setStatus(request.status());
        return activityLog;
    }

    private ActivityLogResponseDto toResponse(ActivityLog activityLog) {
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
