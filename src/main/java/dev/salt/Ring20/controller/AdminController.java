package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.adminDtos.*;
import dev.salt.Ring20.dto.userDtos.UserRequestDto;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;
import dev.salt.Ring20.service.data.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(
        name = "Admin",
        description =
                "Administrative endpoints for managing users, monitoring activity, and viewing system statistics.")
public class AdminController {
    private static final String UNKNOWN_USER = "Unknown user";
    private static final String UNKNOWN_WORKOUT = "Unknown workout";
    private final UserService userService;
    private final FeedbackService feedbackService;
    private final ActivityLogService activityLogService;
    private final AdminService adminService;

    public AdminController(
            UserService userService,
            FeedbackService feedbackService,
            ActivityLogService activityLogService,
            AdminService adminService) {
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.activityLogService = activityLogService;
        this.adminService = adminService;
    }

    //TODO: response String can be a string format -> follows logging convention
    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get admin page",
            description = "Returns a welcome message for administrators.")
    public ResponseEntity<String> adminPage(Authentication authentication) {
        final String name = userService.getByClerkIdOrThrow(authentication.getName()).getName();

        return ResponseEntity.ok(
                "Congrats, "
                        + name
                        + " - you're the admin. Try not to break everything. \uD83D\uDE0E");
    }

    @GetMapping("/users/count")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user counts",
            description = "Retrieves the total number of users and active users.")
    public ResponseEntity<AdminUserCountResponseDto> getUserCount() {
        long total = userService.getUserCount();
        long active = activityLogService.getActiveUserCount();
        //TODO: use the same way of sending ResponseEntity, either .ok(whats in the body) or .ok().body(whats in the body) not both
        return ResponseEntity.ok(new AdminUserCountResponseDto(total, active));
    }

    @GetMapping("/users")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user summaries",
            description = "Retrieves a summary of all registered users.")
    public ResponseEntity<List<AdminUserSummaryResponseDto>> getUsers() {
        //TODO: to have less private methods in controller, mapping can be done in the DTO
        return ResponseEntity.ok(toAdminUserSummaryResponseDto(adminService.getUserSummaries()));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Update user", description = "Updates the details of an existing user.")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id, @Valid @RequestBody UserRequestDto updateData) {
        User updated = adminService.updateUser(id, toUserEntity(updateData));
        return ResponseEntity.ok("User with ID " + updated.getId() + " updated successfully");
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Delete user", description = "Deletes a user from the system.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activity-logs/recent")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get recent activity logs",
            description = "Retrieves the most recent workout activity logs.")
    public ResponseEntity<List<AdminRecentActivityResponseDto>> getRecentActivityLogs() {
        //TODO: to have less private methods in controller, mapping can be done in the DTO
        return ResponseEntity.ok(
                toAdminRecentActivityResponseDto(adminService.getRecentActivityLogs()));
    }

    @GetMapping("/workouts/usage")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get workout usage statistics",
            description = "Retrieves usage statistics for workouts.")
    public ResponseEntity<List<AdminWorkoutUsageResponseDto>> getWorkoutUsage() {
        //TODO: to have less private methods in controller, mapping can be done in the DTO
        return ResponseEntity.ok(toAdminWorkoutUsageResponseDto(adminService.getWorkoutUsage()));
    }

    @GetMapping("/workouts/feedback-summary")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get workout feedback summary",
            description = "Retrieves aggregated feedback statistics for workouts.")
    public ResponseEntity<List<AdminWorkoutFeedbackSummaryResponseDto>>
            getWorkoutFeedbackSummary() {
        //TODO: to have less private methods in controller, mapping can be done in the DTO
        return ResponseEntity.ok(
                toWorkoutFeedbackSummaryDto(feedbackService.getWorkoutFeedbackSummary()));
    }

    @GetMapping("/feedbacks")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get recent feedback",
            description = "Retrieves the most recent workout feedback entries.")
    public ResponseEntity<List<AdminRecentFeedbackResponseDto>> getRecentFeedbackEntries() {
        //TODO: to have less private methods in controller, mapping can be done in the DTO
        return ResponseEntity.ok(
                toAdminRecentFeedbackResponseDto(feedbackService.getRecentFeedbackEntries()));
    }

    @GetMapping("/trainers/overview")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get trainer overview",
            description = "Retrieves overview information for all trainers.")
    public ResponseEntity<List<AdminTrainerOverviewResponseDto>> getTrainerOverview() {
        //TODO: to have less private methods in controller, mapping can be done in the DTO
        return ResponseEntity.ok(
                toAdminTrainerOverviewsponseDto(adminService.getTrainerOverview()));
    }

    //TODO: document private methods
    private List<AdminWorkoutFeedbackSummaryResponseDto> toWorkoutFeedbackSummaryDto(
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

    private List<AdminUserSummaryResponseDto> toAdminUserSummaryResponseDto(UserSummaryData data) {
        return data.users().stream()
                .map(
                        user ->
                                new AdminUserSummaryResponseDto(
                                        user.getId(),
                                        user.getName(),
                                        user.getClerkId(),
                                        user.getRole(),
                                        user.getIntensityLevel(),
                                        user.getTrainerId(),
                                        data.lastCompletedAtByUserId().get(user.getId())))
                .toList();
    }

    private List<AdminRecentActivityResponseDto> toAdminRecentActivityResponseDto(
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

    private List<AdminWorkoutUsageResponseDto> toAdminWorkoutUsageResponseDto(
            WorkoutUsageData data) {
        return data.workouts().stream()
                .map(
                        workout ->
                                new AdminWorkoutUsageResponseDto(
                                        workout.getId(),
                                        workout.getName(),
                                        workout.getTrainer() == null
                                                ? null
                                                : workout.getTrainer().getName(),
                                        data.startedCountByWorkoutId()
                                                .getOrDefault(workout.getId(), 0L),
                                        data.completedCountByWorkoutId()
                                                .getOrDefault(workout.getId(), 0L),
                                        data.lastCompletedAtByWorkoutId().get(workout.getId())))
                .toList();
    }

    //TODO: fix typo
    private List<AdminTrainerOverviewResponseDto> toAdminTrainerOverviewsponseDto(
            TrainerOverviewData data) {
        return data.trainers().stream()
                .map(
                        trainer ->
                                new AdminTrainerOverviewResponseDto(
                                        trainer.getId(),
                                        trainer.getName(),
                                        trainer.getLanguage(),
                                        data.assignedUserCountByTrainerId()
                                                .getOrDefault(trainer.getId(), 0L),
                                        data.workoutCountByTrainerId()
                                                .getOrDefault(trainer.getId(), 0L),
                                        data.enabledWorkoutCountByTrainerId()
                                                .getOrDefault(trainer.getId(), 0L)))
                .toList();
    }

    private List<AdminRecentFeedbackResponseDto> toAdminRecentFeedbackResponseDto(
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

    private User toUserEntity(UserRequestDto request) {
        User user = new User();

        user.setName(request.name());
        user.setIntensityLevel(request.intensityLevel());
        user.setContext(request.context());
        user.setTrainerId(request.trainerId());
        user.setCity(request.city());
        user.setOnboarding(request.onboarding());

        return user;
    }
}
