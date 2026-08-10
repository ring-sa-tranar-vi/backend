package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.entity.ApplicationStatus;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.PaymentStatus;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.OrganizationApplicationRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationApplicationServiceTest {

    @Mock private OrganizationApplicationRepository applicationRepository;
    @Mock private UserService userService;
    @Mock private OrganisationService organisationService;

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
                        .createApplication(
                                "clerk-user",
                                "Organisation AB",
                                "Description",
                                "Stockholm",
                                "Motivation");

        assertEquals(ApplicationStatus.PENDING, created.getApplicationStatus());
        assertEquals(PaymentStatus.PENDING, created.getPaymentStatus());
        assertNotNull(created.getCreatedAt());
        assertNull(created.getReviewedAt());
        verify(organisationService, never()).createOrganisation(any(), any(), any(), any(), any());
    }

    @Test
    void rejectMarksApplicationReviewedWithoutCreatingOrganisation() {
        OrganizationApplication pending = pendingApplication(9L, user(7L));
        when(applicationRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
        when(applicationRepository.save(pending)).thenReturn(pending);

        OrganizationApplication rejected = service().reject(9L);

        assertEquals(ApplicationStatus.REJECTED, rejected.getApplicationStatus());
        assertNotNull(rejected.getReviewedAt());
        verify(organisationService, never()).createOrganisation(any(), any(), any(), any(), any());
    }

    @Test
    void approveCreatesOrganisationForApplicantAndMarksApplicationReviewed() {
        User applicant = user(7L);
        OrganizationApplication pending = pendingApplication(9L, applicant);
        when(applicationRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
        when(applicationRepository.save(pending)).thenReturn(pending);

        OrganizationApplication approved = service().approve(9L);

        verify(organisationService)
                .createOrganisation(
                        eq("Organisation AB"),
                        eq("Description"),
                        eq("Stockholm"),
                        eq(7L),
                        eq("Motivation"));
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
                        .createApplication(
                                "clerk-user",
                                "New organisation",
                                "Description",
                                "Malmo",
                                "Motivation");

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
                applicationRepository, userService, organisationService);
    }
}
