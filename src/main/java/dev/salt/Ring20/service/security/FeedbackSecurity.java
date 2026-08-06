package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.Feedback;
import dev.salt.Ring20.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service("feedbackSecurity")
public class FeedbackSecurity {

    private final FeedbackRepository repository;
    private final SecurityService securityService;

    public FeedbackSecurity(FeedbackRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {
        Feedback feedback = repository.findById(id).orElseThrow();

        return securityService.isOwnerOrAdmin(feedback.getUserId(), clerkId);
    }
}
