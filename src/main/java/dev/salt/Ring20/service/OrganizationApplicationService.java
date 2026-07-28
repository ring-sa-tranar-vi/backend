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
        return repo.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No application found with id: "+ id));
    }

    @Transactional
    public void approve(Long id) {
        getById(id).
                setApplicationStatus(ApplicationStatus.APPROVED);
    }

    @Transactional
    public void reject(Long id) {
        getById(id).
                setApplicationStatus(ApplicationStatus.REJECTED);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
