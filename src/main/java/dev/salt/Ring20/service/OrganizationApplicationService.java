package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.ApplicationStatus;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.PaymentStatus;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.OrganizationApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrganizationApplicationService {

    private final OrganizationApplicationRepository repo;
    private final UserService userService;

    public OrganizationApplicationService(OrganizationApplicationRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    @Transactional
    public OrganizationApplication createApplication(String clerkId, String orgName, String description, String motivation) {
        OrganizationApplication application = new OrganizationApplication();
        try {
            Long userId = Long.valueOf(clerkId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        User user = userService.getUserById(Long.valueOf(clerkId));
        application.setUser(user);
        application.setOrganizationName(orgName);
        application.setDescription(description);
        application.setMotivation(motivation);
        application.setApplicationStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setPaymentStatus(PaymentStatus.PENDING);
        return repo.save(application);
    }

    public List<OrganizationApplication> getAll() {
        return repo.findAll();
    }

    public OrganizationApplication getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("No application found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("No application found with id: " + id);
        }
        repo.deleteById(id);
    }

    @Transactional
    public OrganizationApplication approve(Long id) {
        OrganizationApplication application = getById(id);
        application.setApplicationStatus(ApplicationStatus.APPROVED);
        setReviewedTime(application);
        return repo.save(application);
    }

    @Transactional
    public OrganizationApplication reject(Long id) {
        OrganizationApplication application = getById(id);
        application.setApplicationStatus(ApplicationStatus.REJECTED);
        setReviewedTime(application);
        return repo.save(application);
    }


    private void setReviewedTime(OrganizationApplication application) {
        application.setReviewedAt(LocalDateTime.now());
    }

    public OrganizationApplication updatePaymentStatus(Long id, PaymentStatus status) {
        OrganizationApplication application = getById(id);
        application.setPaymentStatus(status);
        return repo.save(application);

    }
}
