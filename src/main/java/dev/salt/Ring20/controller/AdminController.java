package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.*;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;

import java.util.List;

import dev.salt.Ring20.service.data.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final String UNKNOWN_USER = "Unknown user";
    private static final String UNKNOWN_WORKOUT = "Unknown workout";
    private final UserService service;
    private final FeedbackService feedbackService;
    private final ActivityLogService activityLogService;
    private final AdminService adminService;

    public AdminController(UserService service, FeedbackService feedbackService, ActivityLogService activityLogService, AdminService adminService) {
        this.service = service;
        this.feedbackService = feedbackService;
        this.activityLogService = activityLogService;
        this.adminService = adminService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<String> adminPage(Authentication authentication) {

        String clerkId = getClerkId(authentication);
        final String name = service.getByClerkIdOrThrow(clerkId).getName();

        return ResponseEntity.ok("Congrats, " + name + " - you're the admin. Try not to break everything. \uD83D\uDE0E");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/count")
    public ResponseEntity<AdminUserCountResponseDto> getUserCount() {
        long total = service.getUserCount();
        long active = activityLogService.getActiveUserCount();
        return ResponseEntity.ok(new AdminUserCountResponseDto(total, active));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserSummaryResponseDto>> getUsers() {
        return ResponseEntity.ok(
                toAdminUserSummaryResponseDto(
                adminService.getUserSummaries()
        )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto updateData) {
        User updated = adminService.updateUser(id, toUserEntity(updateData));
        return ResponseEntity.ok("User with ID " + updated.getId() + " updated successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activity-logs/recent")
    public ResponseEntity<List<AdminRecentActivityResponseDto>> getRecentActivityLogs() {
        return ResponseEntity.ok(
                toAdminRecentActivityResponseDto(
                adminService.getRecentActivityLogs()
        )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/workouts/usage")
    public ResponseEntity<List<AdminWorkoutUsageResponseDto>> getWorkoutUsage() {

        return ResponseEntity.ok(
                toAdminWorkoutUsageResponseDto(
                        adminService.getWorkoutUsage()
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/workouts/feedback-summary")
    public ResponseEntity<List<AdminWorkoutFeedbackSummaryResponseDto>> getWorkoutFeedbackSummary() {

        return ResponseEntity.ok(
                toWorkoutFeedbackSummaryDto(
                        feedbackService.getWorkoutFeedbackSummary()
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/feedbacks")
    public ResponseEntity<List<AdminRecentFeedbackResponseDto>> getRecentFeedbackEntries() {

        return ResponseEntity.ok(
                toAdminRecentFeedbackResponseDto(
                feedbackService.getRecentFeedbackEntries()
                ));
    }

    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/trainers/overview")
    public ResponseEntity<List<AdminTrainerOverviewResponseDto>> getTrainerOverview() {
        return ResponseEntity.ok(
                toAdminTrainerOverviewsponseDto(
                        adminService.getTrainerOverview()
                ));
    }

    private Jwt getJwtOrThrow(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt;
    }

    private String getClerkId(Authentication authentication) {
        return getJwtOrThrow(authentication).getSubject();
    }
    private List<AdminWorkoutFeedbackSummaryResponseDto> toWorkoutFeedbackSummaryDto(List<WorkoutFeedbackSummaryData> data) {
        return data.stream().map(summary -> new AdminWorkoutFeedbackSummaryResponseDto(summary.workout().getId(), summary.workout().getName(), summary.feedbackCount(), summary.avgRating(), summary.dislikeRate(), summary.tooHardRate(), summary.status())).toList();
    }
    private List<AdminUserSummaryResponseDto> toAdminUserSummaryResponseDto(UserSummaryData data) {
        return data.users().stream().map(user -> new AdminUserSummaryResponseDto(user.getId(), user.getName(), user.getClerkId(), user.getRole(), user.getIntensityLevel(), user.getTrainerId(), data.lastCompletedAtByUserId().get(user.getId()))).toList();

    }

    private List<AdminRecentActivityResponseDto> toAdminRecentActivityResponseDto(RecentActivityData data) {
        return data.activityLogs().stream().map(activityLog -> new AdminRecentActivityResponseDto(activityLog.getId(), activityLog.getUserId(), data.userNameById().getOrDefault(activityLog.getUserId(), UNKNOWN_USER), activityLog.getWorkoutId(), data.workoutNameById().getOrDefault(activityLog.getWorkoutId(), UNKNOWN_WORKOUT), activityLog.getStatus(), activityLog.getDurationSeconds(), activityLog.getCompletedAt())).toList();

    }

    private List<AdminWorkoutUsageResponseDto> toAdminWorkoutUsageResponseDto(WorkoutUsageData data) {
        return data.workouts().stream().map(workout -> new AdminWorkoutUsageResponseDto(workout.getId(), workout.getName(), workout.getTrainer() == null ? null : workout.getTrainer().getName(), data.startedCountByWorkoutId().getOrDefault(workout.getId(), 0L), data.completedCountByWorkoutId().getOrDefault(workout.getId(), 0L), data.lastCompletedAtByWorkoutId().get(workout.getId()))).toList();

    }

    private List<AdminTrainerOverviewResponseDto> toAdminTrainerOverviewsponseDto(TrainerOverviewData data) {
        return data.trainers().stream().map(trainer -> new AdminTrainerOverviewResponseDto(trainer.getId(), trainer.getName(), trainer.getLanguage(), data.assignedUserCountByTrainerId().getOrDefault(trainer.getId(), 0L), data.workoutCountByTrainerId().getOrDefault(trainer.getId(), 0L), data.enabledWorkoutCountByTrainerId().getOrDefault(trainer.getId(), 0L))).toList();

    }

    private List<AdminRecentFeedbackResponseDto> toAdminRecentFeedbackResponseDto(RecentFeedbackData data) {

        return data.feedbacks().stream().map(feedback -> new AdminRecentFeedbackResponseDto(feedback.getId(), feedback.getUserId(), feedback.getWorkoutId(), feedback.getActivityLogId(), data.workoutNameById().getOrDefault(feedback.getWorkoutId(), UNKNOWN_WORKOUT), feedback.getDifficulty(), feedback.getLiked(), feedback.getRating(), feedback.getComment(), feedback.getCreatedAt())).toList();
    }

    private User toUserEntity(UserRequestDto request){
        User user = new User();

        user.setName(request.name());
        user.setIntensityLevel(request.intensityLevel());
        user.setContext(request.context());
        user.setTrainerId(request.trainerId());
        user.setCity(request.city());

        return user;
    }
}
