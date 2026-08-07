package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.admin.*;
import dev.salt.Ring20.dto.user.UserRequestDto;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.mapper.AdminMapper;
import dev.salt.Ring20.mapper.UserMapper;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;
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

    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get admin page",
            description = "Returns a welcome message for administrators.")
    public ResponseEntity<String> adminPage(Authentication authentication) {
        final String name = userService.getByClerkIdOrThrow(authentication.getName()).getName();

        return ResponseEntity.ok()
                .body(
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

        return ResponseEntity.ok().body(new AdminUserCountResponseDto(total, active));
    }

    @GetMapping("/users")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user summaries",
            description = "Retrieves a summary of all registered users.")
    public ResponseEntity<List<AdminUserSummaryResponseDto>> getUsers() {

        return ResponseEntity.ok()
                .body(AdminMapper.toAdminUserSummaryResponseDto(adminService.getUserSummaries()));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Update user", description = "Updates the details of an existing user.")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id, @Valid @RequestBody UserRequestDto updateData) {
        User updated = adminService.updateUser(id, UserMapper.toUserEntity(updateData));

        return ResponseEntity.ok()
                .body("User with ID " + updated.getId() + " updated successfully");
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
        return ResponseEntity.ok()
                .body(
                        AdminMapper.toAdminRecentActivityResponseDto(
                                adminService.getRecentActivityLogs()));
    }

    @GetMapping("/workouts/usage")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get workout usage statistics",
            description = "Retrieves usage statistics for workouts.")
    public ResponseEntity<List<AdminWorkoutUsageResponseDto>> getWorkoutUsage() {

        return ResponseEntity.ok()
                .body(AdminMapper.toAdminWorkoutUsageResponseDto(adminService.getWorkoutUsage()));
    }

    @GetMapping("/workouts/feedback-summary")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get workout feedback summary",
            description = "Retrieves aggregated feedback statistics for workouts.")
    public ResponseEntity<List<AdminWorkoutFeedbackSummaryResponseDto>>
            getWorkoutFeedbackSummary() {

        return ResponseEntity.ok()
                .body(
                        AdminMapper.toWorkoutFeedbackSummaryDto(
                                feedbackService.getWorkoutFeedbackSummary()));
    }

    @GetMapping("/feedbacks")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get recent feedback",
            description = "Retrieves the most recent workout feedback entries.")
    public ResponseEntity<List<AdminRecentFeedbackResponseDto>> getRecentFeedbackEntries() {

        return ResponseEntity.ok()
                .body(
                        AdminMapper.toAdminRecentFeedbackResponseDto(
                                feedbackService.getRecentFeedbackEntries()));
    }

    @GetMapping("/trainers/overview")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get trainer overview",
            description = "Retrieves overview information for all trainers.")
    public ResponseEntity<List<AdminTrainerOverviewResponseDto>> getTrainerOverview() {

        return ResponseEntity.ok()
                .body(
                        AdminMapper.toAdminTrainerOverviewsResponseDto(
                                adminService.getTrainerOverview()));
    }
}
