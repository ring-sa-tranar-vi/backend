package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.dto.AdminUserCountDto;
import dev.salt.Ring20.dto.AdminRecentActivityDTO;
import dev.salt.Ring20.dto.AdminEventRequestDTO;
import dev.salt.Ring20.dto.AdminEventResponseDTO;
import dev.salt.Ring20.dto.AdminOrganisationRequestDTO;
import dev.salt.Ring20.dto.AdminOrganisationResponseDTO;
import dev.salt.Ring20.dto.AdminTrainerOverviewDTO;
import dev.salt.Ring20.dto.AdminUserSummaryDTO;
import dev.salt.Ring20.dto.AdminWorkoutUsageDTO;
import dev.salt.Ring20.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.AdminService;
import dev.salt.Ring20.service.FeedbackService;
import dev.salt.Ring20.service.UserService;
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

    @Mock private UserService userService;

    @Mock private FeedbackService feedbackService;

    @Mock private ActivityLogService activityLogService;

    @Mock private AdminService adminService;

    @Test
    @DisplayName("getUserCount returns count DTO for admin")
    void getUserCountReturnsCountForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(userService.getUserCount()).thenReturn(42L);
        when(activityLogService.getActiveUserCount()).thenReturn(7L);

        ResponseEntity<AdminUserCountDto> response = controller.getUserCount(auth("clerk_admin"));

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

        ResponseEntity<AdminUserCountDto> response = controller.getUserCount(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, never()).getUserCount();
        verify(activityLogService, never()).getActiveUserCount();
    }

    @Test
    @DisplayName("getUsers returns summaries for admin")
    void getUsersReturnsSummariesForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(adminService.getUserSummaries())
                .thenReturn(List.of(new AdminUserSummaryDTO(1L, "Ada", "clerk_1", "USER", 2, 1L, null)));

        ResponseEntity<List<AdminUserSummaryDTO>> response = controller.getUsers(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getUserSummaries();
    }

    @Test
    @DisplayName("getUsers returns 403 for non-admin")
    void getUsersReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminUserSummaryDTO>> response = controller.getUsers(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getUserSummaries();
    }

    @Test
    @DisplayName("getRecentActivityLogs returns data for admin")
    void getRecentActivityLogsReturnsDataForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(adminService.getRecentActivityLogs())
                .thenReturn(
                        List.of(
                                new AdminRecentActivityDTO(
                                        1L,
                                        2L,
                                        "Ada",
                                        3L,
                                        "Morning Flow",
                                        "COMPLETED",
                                        120,
                                        LocalDateTime.now())));

        ResponseEntity<List<AdminRecentActivityDTO>> response =
                controller.getRecentActivityLogs(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getRecentActivityLogs();
    }

    @Test
    @DisplayName("getRecentActivityLogs returns 403 for non-admin")
    void getRecentActivityLogsReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminRecentActivityDTO>> response =
                controller.getRecentActivityLogs(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getRecentActivityLogs();
    }

    @Test
    @DisplayName("getWorkoutUsage returns data for admin")
    void getWorkoutUsageReturnsDataForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(adminService.getWorkoutUsage())
                .thenReturn(
                        List.of(
                                new AdminWorkoutUsageDTO(
                                        1L,
                                        "Morning Flow",
                                        "Coach A",
                                        10,
                                        8,
                                        LocalDateTime.now())));

        ResponseEntity<List<AdminWorkoutUsageDTO>> response =
                controller.getWorkoutUsage(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getWorkoutUsage();
    }

    @Test
    @DisplayName("getWorkoutUsage returns 403 for non-admin")
    void getWorkoutUsageReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminWorkoutUsageDTO>> response =
                controller.getWorkoutUsage(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getWorkoutUsage();
    }

    @Test
    @DisplayName("getTrainerOverview returns data for admin")
    void getTrainerOverviewReturnsDataForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(adminService.getTrainerOverview())
                .thenReturn(List.of(new AdminTrainerOverviewDTO(1L, "Coach A", "sv", 3, 5, 4)));

        ResponseEntity<List<AdminTrainerOverviewDTO>> response =
                controller.getTrainerOverview(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getTrainerOverview();
    }

    @Test
    @DisplayName("getTrainerOverview returns 403 for non-admin")
    void getTrainerOverviewReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminTrainerOverviewDTO>> response =
                controller.getTrainerOverview(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getTrainerOverview();
    }

    @Test
    @DisplayName("getOrganisations returns data for admin")
    void getOrganisationsReturnsDataForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(adminService.getOrganisations())
                .thenReturn(List.of(new AdminOrganisationResponseDTO(1L, "Org A", "Desc")));

        ResponseEntity<List<AdminOrganisationResponseDTO>> response =
                controller.getOrganisations(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getOrganisations();
    }

    @Test
    @DisplayName("getOrganisations returns 403 for non-admin")
    void getOrganisationsReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminOrganisationResponseDTO>> response =
                controller.getOrganisations(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getOrganisations();
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

        ResponseEntity<String> response = controller.updateUser(5L, updateData, auth("clerk_admin"));

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

    @Test
    @DisplayName("createOrganisation returns 200 for admin")
    void createOrganisationReturnsOkForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);

        AdminOrganisationRequestDTO request = new AdminOrganisationRequestDTO("Org A", "Desc");
        AdminOrganisationResponseDTO responseDto =
                new AdminOrganisationResponseDTO(1L, "Org A", "Desc");
        when(adminService.createOrganisation(request)).thenReturn(responseDto);

        ResponseEntity<AdminOrganisationResponseDTO> response =
                controller.createOrganisation(request, auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        verify(adminService).createOrganisation(request);
    }

    @Test
    @DisplayName("createOrganisation returns 403 for non-admin")
    void createOrganisationReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        AdminOrganisationRequestDTO request = new AdminOrganisationRequestDTO("Org A", "Desc");
        ResponseEntity<AdminOrganisationResponseDTO> response =
                controller.createOrganisation(request, auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).createOrganisation(any());
    }

    @Test
    @DisplayName("createEvent returns 200 for admin")
    void createEventReturnsOkForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);

        AdminEventRequestDTO request =
                new AdminEventRequestDTO("Event A", "Desc", LocalDateTime.now(), 1L);
        AdminEventResponseDTO responseDto =
                new AdminEventResponseDTO(10L, "Event A", "Desc", LocalDateTime.now(), 1L, "Org A");
        when(adminService.createEvent(request)).thenReturn(responseDto);

        ResponseEntity<AdminEventResponseDTO> response =
                controller.createEvent(request, auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().id());
        verify(adminService).createEvent(request);
    }

    @Test
    @DisplayName("createEvent returns 403 for non-admin")
    void createEventReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        AdminEventRequestDTO request =
                new AdminEventRequestDTO("Event A", "Desc", LocalDateTime.now(), 1L);
        ResponseEntity<AdminEventResponseDTO> response =
                controller.createEvent(request, auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).createEvent(any());
    }

    @Test
    @DisplayName("deleteEvent returns 204 for admin")
    void deleteEventReturnsNoContentForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteEvent(3L, auth("clerk_admin"));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService).deleteEvent(3L);
    }

    @Test
    @DisplayName("deleteEvent returns 403 for non-admin")
    void deleteEventReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteEvent(3L, auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).deleteEvent(anyLong());
    }

    @Test
    @DisplayName("deleteOrganisation returns 204 for admin")
    void deleteOrganisationReturnsNoContentForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteOrganisation(2L, auth("clerk_admin"));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService).deleteOrganisation(2L);
    }

    @Test
    @DisplayName("deleteOrganisation returns 403 for non-admin")
    void deleteOrganisationReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteOrganisation(2L, auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).deleteOrganisation(anyLong());
    }

    @Test
    @DisplayName("getEvents returns data for admin")
    void getEventsReturnsDataForAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_admin")).thenReturn(true);
        when(adminService.getEvents())
                .thenReturn(
                        List.of(
                                new AdminEventResponseDTO(
                                        10L,
                                        "Event A",
                                        "Desc",
                                        LocalDateTime.now(),
                                        1L,
                                        "Org A")));

        ResponseEntity<List<AdminEventResponseDTO>> response = controller.getEvents(auth("clerk_admin"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getEvents();
    }

    @Test
    @DisplayName("getEvents returns 403 for non-admin")
    void getEventsReturnsForbiddenForNonAdmin() {
        AdminController controller =
                new AdminController(userService, feedbackService, activityLogService, adminService);
        when(userService.isAdmin("clerk_user")).thenReturn(false);

        ResponseEntity<List<AdminEventResponseDTO>> response = controller.getEvents(auth("clerk_user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(adminService, never()).getEvents();
    }

    private Authentication auth(String subject) {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(subject);
        return auth;
    }
}
