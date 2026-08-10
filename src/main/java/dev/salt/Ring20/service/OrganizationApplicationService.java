package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.enums.ApplicationStatus;
import dev.salt.Ring20.entity.enums.PaymentStatus;
import dev.salt.Ring20.repository.OrganizationApplicationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationApplicationService {

    private static final List<ApplicationStatus> ACTIVE_APPLICATION_STATUSES =
            List.of(ApplicationStatus.PENDING, ApplicationStatus.APPROVED);

    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final UserService userService;
    private final OrganizationService organizationService;

    public OrganizationApplicationService(
            OrganizationApplicationRepository organizationApplicationRepository,
            UserService userService,
            OrganizationService organizationService) {
        this.organizationApplicationRepository = organizationApplicationRepository;
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @Transactional
    public OrganizationApplication createApplication(
            OrganizationApplication application, String clerkId) {
        User user = userService.getByClerkIdOrThrow(clerkId);

        if (organizationService.hasOrganisation(user.getId())) {
            throw new IllegalStateException("User already organizes an organisation");
        }
        if (organizationApplicationRepository.existsByUser_IdAndApplicationStatusIn(
                user.getId(), ACTIVE_APPLICATION_STATUSES)) {
            throw new IllegalStateException(
                    "User already has a pending or approved organisation application");
        }

        application.setUser(user);
        application.setApplicationStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setPaymentStatus(PaymentStatus.PENDING);

        return organizationApplicationRepository.save(application);
    }

    public List<OrganizationApplication> getAll() {
        return organizationApplicationRepository.findAll();
    }

    public OrganizationApplication getById(Long id) {
        return organizationApplicationRepository
                .findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException("No application found with id: " + id));
    }

    @Transactional(readOnly = true)
    public OrganizationApplication getLatestForUser(String clerkId) {
        return organizationApplicationRepository
                .findTopByUser_ClerkIdOrderByCreatedAtDesc(clerkId)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "No organisation application found for current user"));
    }

    @Transactional
    public void delete(Long id) {
        if (!organizationApplicationRepository.existsById(id)) {
            throw new NoSuchElementException("No application found with id: " + id);
        }
        organizationApplicationRepository.deleteById(id);
    }

    @Transactional
    public OrganizationApplication approve(Long id) {
        OrganizationApplication application = getByIdForUpdate(id);

        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application already processed");
        }

        if (organizationService.hasOrganisation(application.getUser().getId())) {
            throw new IllegalStateException("Applicant already organizes an organisation");
        }

        Organization organization = createOrganization(application);
        organizationService.createOrganisation(organization, application.getUser().getId());

        application.setApplicationStatus(ApplicationStatus.APPROVED);
        setReviewedTime(application);

        return organizationApplicationRepository.save(application);
    }

    private Organization createOrganization(OrganizationApplication application) {
        Organization organization = new Organization();
        organization.setName(application.getOrganizationName());
        organization.setDescription(application.getDescription());
        organization.setOrgCity(application.getCity());
        organization.setMotivation(application.getMotivation());
        return organization;
    }

    @Transactional
    public OrganizationApplication reject(Long id) {
        OrganizationApplication application = getByIdForUpdate(id);
        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application already processed");
        }
        application.setApplicationStatus(ApplicationStatus.REJECTED);
        setReviewedTime(application);
        return organizationApplicationRepository.save(application);
    }

    private void setReviewedTime(OrganizationApplication application) {
        application.setReviewedAt(LocalDateTime.now());
    }

    @Transactional
    public OrganizationApplication updatePaymentStatus(Long id, PaymentStatus status) {
        OrganizationApplication application = getByIdForUpdate(id);
        application.setPaymentStatus(status);
        return organizationApplicationRepository.save(application);
    }

    private OrganizationApplication getByIdForUpdate(Long id) {
        return organizationApplicationRepository
                .findByIdForUpdate(id)
                .orElseThrow(
                        () -> new NoSuchElementException("No application found with id: " + id));
    }
}
