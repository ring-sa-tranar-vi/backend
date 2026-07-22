package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.dto.AdminRecentActivityResponseDto;
import dev.salt.Ring20.dto.AdminTrainerOverviewResponseDto;
import dev.salt.Ring20.dto.AdminUserCountResponseDto;
import dev.salt.Ring20.dto.AdminUserSummaryResponseDto;
import dev.salt.Ring20.dto.AdminWorkoutUsageResponseDto;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import dev.salt.Ring20.service.data.RecentActivityData;
import dev.salt.Ring20.service.data.TrainerOverviewData;
import dev.salt.Ring20.service.data.UserSummaryData;
import dev.salt.Ring20.service.data.WorkoutUsageData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Tests")
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private AdminService adminService;

    @Test
    @DisplayName("getUserCount returns count DTO for admin")
    void getUserCountReturnsCountForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(userService.getUserCount()).thenReturn(42L);
        when(activityLogService.getActiveUserCount()).thenReturn(7L);

        ResponseEntity<AdminUserCountResponseDto> response =
                controller.getUserCount(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(42L, response.getBody().count());
        assertEquals(7L, response.getBody().activeCount());
    }

    @Test
    @DisplayName("getUserCount returns 403 for non-admin")
    void getUserCountReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<AdminUserCountResponseDto> response =
                controller.getUserCount(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, never()).getUserCount();
        verify(activityLogService, never()).getActiveUserCount();
    }

    @Test
    @DisplayName("getUsers returns summaries for admin")
    void getUsersReturnsSummariesForAdmin() {

        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

        when(userService.isAdmin("clerk_admin"))
                .thenReturn(true);

        User user = new User();
        user.setId(1L);
        user.setName("Ada");
        user.setClerkId("clerk_1");
        user.setRole(UserRole.USER);
        user.setIntensityLevel(2);
        user.setTrainerId(1L);

        when(adminService.getUserSummaries())
                .thenReturn(
                        new UserSummaryData(
                                List.of(user),
                                Map.of(1L, null)
                        )
                );

        ResponseEntity<List<AdminUserSummaryResponseDto>> response =
                controller.getUsers(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminUserSummaryResponseDto dto = response.getBody().get(0);

        assertEquals(1L, dto.id());
        assertEquals("Ada", dto.name());

        verify(adminService).getUserSummaries();
    }

    @Test
    @DisplayName("getUsers returns 403 for non-admin")
    void getUsersReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminUserSummaryResponseDto>> response =
                controller.getUsers(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getUserSummaries();
    }

    @Test
    @DisplayName("getRecentActivityLogs returns data for admin")
    void getRecentActivityLogsReturnsDataForAdmin() {

        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

        when(userService.isAdmin("clerk_admin"))
                .thenReturn(true);
        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setUserId(2L);
        activityLog.setWorkoutId(3L);
        activityLog.setStatus("COMPLETED");
        activityLog.setDurationSeconds(120);
        activityLog.setCompletedAt(LocalDateTime.now());

        when(adminService.getRecentActivityLogs())
                .thenReturn(
                        new RecentActivityData(
                                List.of(activityLog),
                                Map.of(2L, "Ada"),
                                Map.of(3L, "Morning Flow")
                        )
                );

        ResponseEntity<List<AdminRecentActivityResponseDto>> response =
                controller.getRecentActivityLogs(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminRecentActivityResponseDto dto = response.getBody().get(0);

        assertEquals(1L, dto.id());
        assertEquals(2L, dto.userId());
        assertEquals("Ada", dto.userName());
        assertEquals("Morning Flow", dto.workoutName());

        verify(adminService).getRecentActivityLogs();

    }

    @Test
    @DisplayName("getRecentActivityLogs returns 403 for non-admin")
    void getRecentActivityLogsReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminRecentActivityResponseDto>> response =
                controller.getRecentActivityLogs(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getRecentActivityLogs();
    }

    @Test
    @DisplayName("getWorkoutUsage returns data for admin")
    void getWorkoutUsageReturnsDataForAdmin() {

        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

        when(userService.isAdmin("clerk_admin"))
                .thenReturn(true);

        Workout workout = new Workout();
        workout.setId(1L);
        workout.setName("Morning Flow");

        Trainer trainer = new Trainer();
        trainer.setName("Coach A");
        workout.setTrainer(trainer);

        when(adminService.getWorkoutUsage())
                .thenReturn(
                        new WorkoutUsageData(
                                List.of(workout),
                                Map.of(1L, 10L),
                                Map.of(1L, 8L),
                                Map.of(1L, LocalDateTime.now())
                        )
                );

        ResponseEntity<List<AdminWorkoutUsageResponseDto>> response =
                controller.getWorkoutUsage(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminWorkoutUsageResponseDto dto = response.getBody().get(0);

        assertEquals(1L, dto.workoutId());
        assertEquals("Morning Flow", dto.workoutName());
        assertEquals("Coach A", dto.trainerName());
        assertEquals(10L, dto.startedCount());
        assertEquals(8L, dto.completedCount());

        verify(adminService).getWorkoutUsage();
    }

    @Test
    @DisplayName("getWorkoutUsage returns 403 for non-admin")
    void getWorkoutUsageReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminWorkoutUsageResponseDto>> response =
                controller.getWorkoutUsage(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getWorkoutUsage();
    }

    @Test
    @DisplayName("getTrainerOverview returns data for admin")
    void getTrainerOverviewReturnsDataForAdmin() {

        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

        when(userService.isAdmin("clerk_admin"))
                .thenReturn(true);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Coach A");
        trainer.setLanguage("sv");

        when(adminService.getTrainerOverview())
                .thenReturn(
                        new TrainerOverviewData(
                                List.of(trainer),
                                Map.of(1L, 3L),
                                Map.of(1L, 5L),
                                Map.of(1L, 4L)
                        )
                );

        ResponseEntity<List<AdminTrainerOverviewResponseDto>> response =
                controller.getTrainerOverview(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminTrainerOverviewResponseDto dto = response.getBody().get(0);

        assertEquals(1L, dto.trainerId());
        assertEquals("Coach A", dto.trainerName());
        assertEquals("sv", dto.language());
        assertEquals(3L, dto.assignedUserCount());
        assertEquals(5L, dto.workoutCount());
        assertEquals(4L, dto.enabledWorkoutCount());

        verify(adminService).getTrainerOverview();
    }

    @Test
    @DisplayName("getTrainerOverview returns 403 for non-admin")
    void getTrainerOverviewReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminTrainerOverviewResponseDto>> response =
                controller.getTrainerOverview(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getTrainerOverview();
    }

    @Test
    @DisplayName("updateUser returns 200 and delegates to service for admin")
    void updateUserReturnsOkForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);

        User updateData = new User();
        updateData.setName("Updated Name");

        User updated = new User();
        updated.setId(5L);
        when(adminService.updateUser(5L, updateData)).thenReturn(updated);

        ResponseEntity<String> response =
                controller.updateUser(5L, updateData, auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User with ID 5 updated successfully", response.getBody());
        verify(adminService).updateUser(5L, updateData);
    }

    @Test
    @DisplayName("updateUser returns 403 for non-admin")
    void updateUserReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        User updateData = new User();
        ResponseEntity<String> response = controller.updateUser(5L, updateData, auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).updateUser(anyLong(), any());
    }

    @Test
    @DisplayName("deleteUser returns 204 and delegates to service for admin")
    void deleteUserReturnsNoContentForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteUser(7L, auth("clerk_admin"));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService).deleteUser(7L);
    }

    private Authentication auth(String subject) {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(subject);
        return auth;
    }
}
