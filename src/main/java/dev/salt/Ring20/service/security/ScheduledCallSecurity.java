package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.ScheduledCall;
import dev.salt.Ring20.repository.ScheduledCallRepository;
import org.springframework.stereotype.Service;

@Service("scheduledCallSecurity")
public class ScheduledCallSecurity {

    private final ScheduledCallRepository repository;
    private final SecurityService securityService;

    public ScheduledCallSecurity(
            ScheduledCallRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {

        ScheduledCall call = repository.findById(id).orElseThrow();

        return call.getUserId().equals(securityService.currentUserId(clerkId));
    }
}
