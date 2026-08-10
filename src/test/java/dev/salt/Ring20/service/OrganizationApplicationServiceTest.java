package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.organization.OrganizationApplicationRequestDto;
import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.enums.ApplicationStatus;
import dev.salt.Ring20.entity.enums.PaymentStatus;
import dev.salt.Ring20.mapper.OrganizationApplicationMapper;
import dev.salt.Ring20.repository.OrganizationApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationApplicationServiceTest {

    @Mock
    private OrganizationApplicationRepository applicationRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrganizationService organizationService;

    @Test
    void returnsLatestApplicationForCurrentUser() {
        OrganizationApplication expected = new OrganizationApplication();
        expected.setId(17L);
        when(applicationRepository.findTopByUser_ClerkIdOrderByCreatedAtDesc("clerk-user"))
                .thenReturn(Optional.of(expected));
        var service = service();

        assertEquals(17L, service.getLatestForUser("clerk-user").getId());
    }

    @Test
    void latestApplicationIsNotFoundWhenUserHasNeverApplied() {
        when(applicationRepository.findTopByUser_ClerkIdOrderByCreatedAtDesc("clerk-user"))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service().getLatestForUser("clerk-user"));
    }

    @Test
    void createApplicationOnlyCreatesPendingApplication() {
        User user = user(7L);
        when(userService.getByClerkIdOrThrow("clerk-user")).thenReturn(user);
        when(applicationRepository.save(any(OrganizationApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationApplication created =
                service()
                        .createApplication(OrganizationApplicationMapper.toEntity(
                                new OrganizationApplicationRequestDto(
                                        "Organisation AB",
                                        "Description",
                                        "Stockholm",
                                        "Motivation")), "clerk-user");

        assertEquals(ApplicationStatus.PENDING, created.getApplicationStatus());
        assertEquals(PaymentStatus.PENDING, created.getPaymentStatus());
        assertNotNull(created.getCreatedAt());
        assertNull(created.getReviewedAt());
        verify(organizationService, never()).createOrganisation(any(), any());
    }

    @Test
    void rejectMarksApplicationReviewedWithoutCreatingOrganisation() {
        OrganizationApplication pending = pendingApplication(9L, user(7L));
        when(applicationRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
        when(applicationRepository.save(pending)).thenReturn(pending);

        OrganizationApplication rejected = service().reject(9L);

        assertEquals(ApplicationStatus.REJECTED, rejected.getApplicationStatus());
        assertNotNull(rejected.getReviewedAt());
        verify(organizationService, never()).createOrganisation(any(), any() );
    }

    @Test
    void approveCreatesOrganisationForApplicantAndMarksApplicationReviewed() {
        User applicant = user(7L);
        OrganizationApplication pending = pendingApplication(9L, applicant);
        when(applicationRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
        when(applicationRepository.save(pending)).thenReturn(pending);

        OrganizationApplication approved = service().approve(9L);

        verify(organizationService)
                .createOrganisation(any(Organization.class), eq(7L)
                );
        assertEquals(ApplicationStatus.APPROVED, approved.getApplicationStatus());
        assertNotNull(approved.getReviewedAt());
    }

    @Test
    void rejectedApplicationDoesNotBlockAReplacementApplication() {
        User user = user(7L);
        when(userService.getByClerkIdOrThrow("clerk-user")).thenReturn(user);
        when(applicationRepository.save(any(OrganizationApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationApplication replacement =
                service()
                        .createApplication(OrganizationApplicationMapper.toEntity(new OrganizationApplicationRequestDto(
                                        "New organisation",
                                        "Description",
                                        "Malmo",
                                        "Motivation"
                                )),
                                "clerk-user"
                        );

        assertEquals(ApplicationStatus.PENDING, replacement.getApplicationStatus());
        verify(applicationRepository)
                .existsByUser_IdAndApplicationStatusIn(
                        eq(7L),
                        eq(
                                java.util.List.of(
                                        ApplicationStatus.PENDING, ApplicationStatus.APPROVED)));
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private OrganizationApplication pendingApplication(Long id, User user) {
        OrganizationApplication application = new OrganizationApplication();
        application.setId(id);
        application.setUser(user);
        application.setOrganizationName("Organisation AB");
        application.setDescription("Description");
        application.setCity("Stockholm");
        application.setMotivation("Motivation");
        application.setApplicationStatus(ApplicationStatus.PENDING);
        application.setPaymentStatus(PaymentStatus.PENDING);
        return application;
    }

    private OrganizationApplicationService service() {
        return new OrganizationApplicationService(
                applicationRepository, userService, organizationService);
    }
}
