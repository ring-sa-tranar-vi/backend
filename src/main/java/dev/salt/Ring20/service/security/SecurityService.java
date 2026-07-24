package dev.salt.Ring20.service.security;

import dev.salt.Ring20.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private final UserService userService;

    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    public Long currentUserId(String clerkId) {
        return userService.getInternalUserId(clerkId);
    }

    public boolean isCurrentUser(Long userId, String clerkId) {
        Long currentUserId = userService.getInternalUserId(clerkId);
        return currentUserId.equals(userId);
    }

    public boolean isAdmin(String clerkId) {
        return userService.isAdmin(clerkId);
    }

    public boolean isOwnerOrAdmin(Long userId, String clerkId) {
        return isCurrentUser(userId, clerkId)
                || isAdmin(clerkId);
    }
}
