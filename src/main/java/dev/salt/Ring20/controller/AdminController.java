package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.AdminRecentActivityResponseDto;
import dev.salt.Ring20.dto.AdminRecentFeedbackResponseDto;
import dev.salt.Ring20.dto.AdminTrainerOverviewResponseDto;
import dev.salt.Ring20.dto.AdminUserCountResponseDto;
import dev.salt.Ring20.dto.AdminUserSummaryResponseDto;
import dev.salt.Ring20.dto.AdminWorkoutFeedbackSummaryResponseDto;
import dev.salt.Ring20.dto.AdminWorkoutUsageResponseDto;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;

import java.util.List;

import dev.salt.Ring20.service.data.RecentActivityData;
import dev.salt.Ring20.service.data.TrainerOverviewData;
import dev.salt.Ring20.service.data.UserSummaryData;
import dev.salt.Ring20.service.data.WorkoutUsageData;
import org.springframework.http.ResponseEntity;
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

    public AdminController(
            UserService service,
            FeedbackService feedbackService,
            ActivityLogService activityLogService,
            AdminService adminService) {
        this.service = service;
        this.feedbackService = feedbackService;
        this.activityLogService = activityLogService;
        this.adminService = adminService;
    }

    private Jwt getJwtOrThrow(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt;
    }

    private String getClerkId(Authentication authentication) {
        return getJwtOrThrow(authentication).getSubject();
    }

    @GetMapping
    public ResponseEntity<String> adminPage(Authentication authentication) {

        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        final String name = service.getByClerkIdOrThrow(clerkId).getName();

        return ResponseEntity.ok(
                "Congrats, "
                        + name
                        + " - you're the admin. Try not to break everything. \uD83D\uDE0E");
    }

    @GetMapping("/users/count")
    public ResponseEntity<AdminUserCountResponseDto> getUserCount(Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        long total = service.getUserCount();
        long active = activityLogService.getActiveUserCount();
        return ResponseEntity.ok(new AdminUserCountResponseDto(total, active));
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserSummaryResponseDto>> getUsers(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }


        UserSummaryData data = adminService.getUserSummaries();

        return ResponseEntity.ok(
                toAdminUserSummaryResponseDto(data)
        );
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

    private List<AdminRecentActivityResponseDto> toAdminRecentActivityResponseDto(RecentActivityData data) {
        return data.activityLogs().stream()
                .map(
                        activityLog ->
                                new AdminRecentActivityResponseDto(
                                        activityLog.getId(),
                                        activityLog.getUserId(),
                                        data.userNameById().getOrDefault(
                                                activityLog.getUserId(), UNKNOWN_USER),
                                        activityLog.getWorkoutId(),
                                        data.workoutNameById().getOrDefault(
                                                activityLog.getWorkoutId(), UNKNOWN_WORKOUT),
                                        activityLog.getStatus(),
                                        activityLog.getDurationSeconds(),
                                        activityLog.getCompletedAt()))
                .toList();

    }

    private List<AdminWorkoutUsageResponseDto> toAdminWorkoutUsageResponseDto(WorkoutUsageData data) {
        return data.workouts().stream()
                .map(workout ->
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
                                data.lastCompletedAtByWorkoutId()
                                        .get(workout.getId())
                        ))
                .toList();

    }

    private List<AdminTrainerOverviewResponseDto> toAdminTrainerOverviewsponseDto(TrainerOverviewData data) {
        return data.trainers().stream()
                .map(
                        trainer ->
                                new AdminTrainerOverviewResponseDto(
                                        trainer.getId(),
                                        trainer.getName(),
                                        trainer.getLanguage(),
                                        data.assignedUserCountByTrainerId().getOrDefault(
                                                trainer.getId(), 0L),
                                        data.workoutCountByTrainerId().getOrDefault(trainer.getId(), 0L),
                                        data.enabledWorkoutCountByTrainerId().getOrDefault(
                                                trainer.getId(), 0L)))
                .toList();

    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @RequestBody dev.salt.Ring20.entity.User updateData,
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        dev.salt.Ring20.entity.User updated = adminService.updateUser(id, updateData);
        return ResponseEntity.ok("User with ID " + updated.getId() + " updated successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activity-logs/recent")
    public ResponseEntity<List<AdminRecentActivityResponseDto>> getRecentActivityLogs(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }
        RecentActivityData data = adminService.getRecentActivityLogs();

        return ResponseEntity.ok(toAdminRecentActivityResponseDto(data));
    }

    @GetMapping("/workouts/usage")
    public ResponseEntity<List<AdminWorkoutUsageResponseDto>> getWorkoutUsage(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        WorkoutUsageData data = adminService.getWorkoutUsage();

        return ResponseEntity.ok(
                toAdminWorkoutUsageResponseDto(data)
        );
    }

    @GetMapping("/workouts/feedback-summary")
    public ResponseEntity<List<AdminWorkoutFeedbackSummaryResponseDto>> getWorkoutFeedbackSummary(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(feedbackService.getWorkoutFeedbackSummary());
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<List<AdminRecentFeedbackResponseDto>> getRecentFeedbackEntries(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(feedbackService.getRecentFeedbackEntries());
    }

    @GetMapping("/trainers/overview")
    public ResponseEntity<List<AdminTrainerOverviewResponseDto>> getTrainerOverview(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }
        TrainerOverviewData data = adminService.getTrainerOverview();
        return ResponseEntity.ok(toAdminTrainerOverviewsponseDto(data));
    }
}
