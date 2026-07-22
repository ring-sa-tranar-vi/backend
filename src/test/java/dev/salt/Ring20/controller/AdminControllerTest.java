package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.dto.AdminRecentActivityResponseDto;
import dev.salt.Ring20.dto.AdminTrainerOverviewResponseDto;
import dev.salt.Ring20.dto.AdminUserCountResponseDto;
import dev.salt.Ring20.dto.AdminUserSummaryResponseDto;
import dev.salt.Ring20.dto.AdminWorkoutUsageResponseDto;
import dev.salt.Ring20.dto.UserRequestDto;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    @DisplayName("getUserCount returns count DTO")
    void getUserCountReturnsCount() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.getUserCount()).thenReturn(42L);
        when(activityLogService.getActiveUserCount()).thenReturn(7L);

        ResponseEntity<AdminUserCountResponseDto> response = controller.getUserCount();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(42L, response.getBody().count());
        assertEquals(7L, response.getBody().activeCount());
    }

    @Test
    @DisplayName("getUsers returns summaries")
    void getUsersReturnsSummaries() {
        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

        User user = new User();
        user.setId(1L);
        user.setName("Ada");
        user.setClerkId("clerk_1");
        user.setRole(UserRole.USER);
        user.setIntensityLevel(2);
        user.setTrainerId(1L);

        Map<Long, LocalDateTime> lastCompletedAtByUserId = new HashMap<>();
        lastCompletedAtByUserId.put(1L, null);

        when(adminService.getUserSummaries())
                .thenReturn(
                        new UserSummaryData(
                                List.of(user),
                                lastCompletedAtByUserId
                        )
                );

        ResponseEntity<List<AdminUserSummaryResponseDto>> response = controller.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminUserSummaryResponseDto dto = response.getBody().get(0);

        assertEquals(1L, dto.id());
        assertEquals("Ada", dto.name());

        verify(adminService).getUserSummaries();
    }

    @Test
    @DisplayName("getRecentActivityLogs returns data")
    void getRecentActivityLogsReturnsData() {
        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

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
                controller.getRecentActivityLogs();

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
    @DisplayName("getWorkoutUsage returns data")
    void getWorkoutUsageReturnsData() {
        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

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

        ResponseEntity<List<AdminWorkoutUsageResponseDto>> response = controller.getWorkoutUsage();

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
    @DisplayName("getTrainerOverview returns data")
    void getTrainerOverviewReturnsData() {
        AdminController controller =
                new AdminController(
                        userService,
                        feedbackService,
                        activityLogService,
                        adminService
                );

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
                controller.getTrainerOverview();

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
    @DisplayName("updateUser returns 200 and delegates to service")
    void updateUserReturnsOk() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);

        UserRequestDto updateData = new UserRequestDto("Updated Name", 2, "context", 1L, "Stockholm");

        User updated = new User();
        updated.setId(5L);
        when(adminService.updateUser(eq(5L), any(User.class))).thenReturn(updated);

        ResponseEntity<String> response = controller.updateUser(5L, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User with ID 5 updated successfully", response.getBody());
        verify(adminService).updateUser(eq(5L), any(User.class));
    }

    @Test
    @DisplayName("deleteUser returns 204 and delegates to service")
    void deleteUserReturnsNoContent() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);

        ResponseEntity<Void> response = controller.deleteUser(7L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService).deleteUser(7L);
    }
}
