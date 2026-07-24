package dev.salt.Ring20.service.security;

import dev.salt.Ring20.service.UserService;
import org.springframework.stereotype.Service;

@Service("userSecurity")
public class UserSecurity {

    private final UserService userService;

    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean isOwner(Long userId, String clerkId) {
        return userService.findByClerkId(clerkId)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }
}
