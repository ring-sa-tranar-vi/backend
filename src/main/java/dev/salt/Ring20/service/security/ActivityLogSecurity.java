package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

@Service("activityLogSecurity")
public class ActivityLogSecurity {

    private final ActivityLogRepository repository;
    private final SecurityService securityService;

    public ActivityLogSecurity(ActivityLogRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {

        ActivityLog log = repository.findById(id).orElseThrow();

        return securityService.isOwnerOrAdmin(log.getUserId(), clerkId);
    }
}
