package dev.salt.Ring20.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private static final String DEFAULT_DISPLAY_NAME = "No name entered";

    private final UserRepository userRepository;
    private final int STARTING_INTENSITY = 2;
    private final MeterRegistry meterRegistry;

    public UserService(UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
    }

    public boolean isAdmin(String clerkID) {
        return getByClerkIdOrThrow(clerkID).getRole().equals("ADMIN");
    }

    private String sanitizeDisplayName(String name) {
        return (name == null || name.isBlank()) ? DEFAULT_DISPLAY_NAME : name.trim();
    }

    private User normalizeDisplayNameIfMissing(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(DEFAULT_DISPLAY_NAME);
            return userRepository.save(user);
        }

        return user;
    }

    public User createUser(String clerkId, String name) {
        String displayName = sanitizeDisplayName(name);
        boolean hasRealDisplayName = !DEFAULT_DISPLAY_NAME.equals(displayName);

        return userRepository
                .findByClerkId(clerkId)
                .map(
                        existingUser -> {
                            boolean missingName =
                                    existingUser.getName() == null
                                            || existingUser.getName().isBlank();
                            boolean hasPlaceholder =
                                    DEFAULT_DISPLAY_NAME.equals(existingUser.getName());

                            if (missingName || (hasPlaceholder && hasRealDisplayName)) {
                                existingUser.setName(displayName);
                                return userRepository.save(existingUser);
                            }

                            return existingUser;
                        })
                .orElseGet(
                        () -> {
                            meterRegistry.counter("app.users.created").increment();
                            return userRepository.save(
                                    new User(displayName, STARTING_INTENSITY, "", clerkId));
                        });
    }

    public Optional<User> findByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId);
    }

    public User getByClerkIdOrThrow(String clerkId) {
        return userRepository
                .findByClerkId(clerkId)
                .map(this::normalizeDisplayNameIfMissing)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    public User updateUserPreferencesByClerkId(
            String clerkId, String name, int intensityLevel, String context, Long trainerId) {
        if (trainerId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Trainer is required");
        }

        User user = getByClerkIdOrThrow(clerkId);

        user.setName(sanitizeDisplayName(name));
        user.setIntensityLevel(intensityLevel);
        user.setContext(context);
        user.setTrainerId(trainerId);
        meterRegistry.counter("app.users.updated").increment();
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    public long getUserCount() {
        return userRepository.count();
    }
}