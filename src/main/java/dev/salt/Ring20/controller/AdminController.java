package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.AdminRecentActivityDTO;
import dev.salt.Ring20.dto.AdminRecentFeedbackDto;
import dev.salt.Ring20.dto.AdminTrainerOverviewDTO;
import dev.salt.Ring20.dto.AdminUserCountDto;
import dev.salt.Ring20.dto.AdminUserSummaryDTO;
import dev.salt.Ring20.dto.AdminWorkoutFeedbackSummaryDto;
import dev.salt.Ring20.dto.AdminWorkoutUsageDTO;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = {"http://localhost:5173", "https://frontend-training.up.railway.app"})
public class AdminController {
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

    @GetMapping("/workouts/feedback-summary")
    public ResponseEntity<List<AdminWorkoutFeedbackSummaryDto>> getWorkoutFeedbackSummary(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(feedbackService.getWorkoutFeedbackSummary());
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<List<AdminRecentFeedbackDto>> getRecentFeedbackEntries(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(feedbackService.getRecentFeedbackEntries());
    }

    @GetMapping("/users/count")
    public ResponseEntity<AdminUserCountDto> getUserCount(Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        long total = service.getUserCount();
        long active = activityLogService.getActiveUserCount();
        return ResponseEntity.ok(new AdminUserCountDto(total, active));
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserSummaryDTO>> getUsers(Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(adminService.getUserSummaries());
    }

    @GetMapping("/activity-logs/recent")
    public ResponseEntity<List<AdminRecentActivityDTO>> getRecentActivityLogs(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(adminService.getRecentActivityLogs());
    }

    @GetMapping("/workouts/usage")
    public ResponseEntity<List<AdminWorkoutUsageDTO>> getWorkoutUsage(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(adminService.getWorkoutUsage());
    }

    @GetMapping("/trainers/overview")
    public ResponseEntity<List<AdminTrainerOverviewDTO>> getTrainerOverview(
            Authentication authentication) {
        String clerkId = getClerkId(authentication);

        if (!service.isAdmin(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(adminService.getTrainerOverview());
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
}
