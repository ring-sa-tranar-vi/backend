package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

// TODO: not consistent as with the SecurityService
@Service
public class ActivityLogSecurityService {

    private final ActivityLogRepository repository;
    private final SecurityService securityService;

    public ActivityLogSecurityService(
            ActivityLogRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {

        ActivityLog log = repository.findById(id).orElseThrow();

        return securityService.isOwnerOrAdmin(log.getUserId(), clerkId);
    }
}
