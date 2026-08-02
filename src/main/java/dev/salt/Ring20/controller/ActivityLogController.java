package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.ActivityLogCreateRequestDto;
import dev.salt.Ring20.dto.ActivityLogResponseDto;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-logs")
@Tag(
        name = "Activity Log",
        description =
                "Endpoints for managing user workout activities and tracking daily completion.")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final SecurityService securityService;

    public ActivityLogController(
            ActivityLogService activityLogService, SecurityService securityService) {
        this.activityLogService = activityLogService;
        this.securityService = securityService;
    }

    @GetMapping("/users/{userId}/has-completed-today")
    @PreAuthorize("@securityService.isOwnerOrAdmin(#userId, authentication.name)")
    @Operation(
            summary = "Get if workout was completed today",
            description =
                    "Checks whether the user has completed a workout activity on the current day.")
    public ResponseEntity<Map<String, Boolean>> hasCompletedWorkoutToday(
            @PathVariable Long userId) {
        boolean hasCompleted = activityLogService.hasCompletedWorkoutToday(userId);
        return ResponseEntity.ok(Map.of("hasCompletedToday", hasCompleted));
    }

    @PostMapping
    @Operation(
            summary = "Create activity log",
            description = "Creates a new workout activity log for a user")
    public ResponseEntity<ActivityLogResponseDto> createActivityLog(
            @Valid @RequestBody ActivityLogCreateRequestDto activityLogRequest,
            Authentication authentication) {
        ActivityLog activityLog = toEntity(activityLogRequest);

        activityLog.setUserId(securityService.currentUserId(authentication.getName()));

        ActivityLog created = activityLogService.createActivityLog(activityLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("@activityLogSecurityService.canModify(#id, authentication.name)")
    @Operation(
            summary = "Complete activity log",
            description = "Marks an existing activity log as completed.")
    public ResponseEntity<ActivityLogResponseDto> completeActivityLog(@PathVariable Long id) {

        ActivityLog completed = activityLogService.completeActivityLog(id);
        return ResponseEntity.ok().body(toResponse(completed));
    }

    private ActivityLog toEntity(ActivityLogCreateRequestDto request) {
        ActivityLog activityLog = new ActivityLog();
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
