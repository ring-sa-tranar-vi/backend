package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.ApplicationStatus;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.PaymentStatus;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.OrganizationApplicationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationApplicationService {

    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final UserService userService;
    private final OrganisationService organisationService;

    public OrganizationApplicationService(
            OrganizationApplicationRepository organizationApplicationRepository,
            UserService userService,
            OrganisationService organisationService) {
        this.organizationApplicationRepository = organizationApplicationRepository;
        this.userService = userService;
        this.organisationService = organisationService;
    }

    @Transactional
    public OrganizationApplication createApplication(
            String clerkId, String orgName, String description, String city, String motivation) {
        OrganizationApplication application = new OrganizationApplication();
        User user = userService.getByClerkIdOrThrow(clerkId);

        application.setUser(user);
        application.setOrganizationName(orgName);
        application.setDescription(description);
        application.setCity(city);
        application.setMotivation(motivation);
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

    @Transactional
    public void delete(Long id) {
        if (!organizationApplicationRepository.existsById(id)) {
            throw new NoSuchElementException("No application found with id: " + id);
        }
        organizationApplicationRepository.deleteById(id);
    }

    @Transactional
    public OrganizationApplication approve(Long id) {
        OrganizationApplication application = getById(id);
        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application already processed");
        }
        application.setApplicationStatus(ApplicationStatus.APPROVED);
        setReviewedTime(application);
        organisationService.createOrganisation(
                application.getOrganizationName(),
                application.getDescription(),
                application.getCity(),
                application.getUser().getId(),
                application.getMotivation());
        return organizationApplicationRepository.save(application);
    }

    @Transactional
    public OrganizationApplication reject(Long id) {
        OrganizationApplication application = getById(id);
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

    public OrganizationApplication updatePaymentStatus(Long id, PaymentStatus status) {
        OrganizationApplication application = getById(id);
        application.setPaymentStatus(status);
        return organizationApplicationRepository.save(application);
    }
}
