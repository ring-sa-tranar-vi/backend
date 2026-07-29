package dev.salt.Ring20.service.security;

import dev.salt.Ring20.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service("securityService")
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
        return currentUserId != null && currentUserId.equals(userId);
    }

    public boolean isAdmin(String clerkId) {
        return userService.isAdmin(clerkId);
    }
    public boolean isSuperAdmin(String clerkId) {
        return userService.isSuperAdmin(clerkId);
    }

    public boolean isOrganizer(String clerkId) {
        return userService.isOrganizer(clerkId);
    }

    public boolean isOwnerOrAdmin(Long userId, String clerkId) {
        return isCurrentUser(userId, clerkId) || isAdmin(clerkId);
    }

    public boolean isAdminIfAuthenticated(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }
        return userService.isAdmin(jwt.getSubject());
    }
}
