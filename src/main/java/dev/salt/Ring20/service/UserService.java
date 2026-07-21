package dev.salt.Ring20.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private static final String DEFAULT_DISPLAY_NAME = "No name entered";

    private final UserRepository userRepository;
    private final int STARTING_INTENSITY = 2;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            String clerkId,
            String name,
            int intensityLevel,
            String context,
            Long trainerId,
            String city) {
        if (trainerId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Trainer is required");
        }

        User user = getByClerkIdOrThrow(clerkId);

        user.setName(sanitizeDisplayName(name));
        user.setIntensityLevel(intensityLevel);
        user.setContext(context);
        user.setTrainerId(trainerId);
        user.setCity(city);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    public List<Organisation> getUserOrgsById(Long id) {
        User user = getUserById(id);
        return user.getFollowedOrganisations();
    }

    public List<Event> getUserEventsById(Long id) {
        User user = getUserById(id);
        return user.getAttendingEvents();
    }

    public User addFollowOrganization(Long id, Organisation org) {
        User user = getUserById(id);
        user.getFollowedOrganisations().add(org);
        int followCount = org.getUsersFollowing() + 1;
        return userRepository.save(user);
    }

    public User addAttendEvent(Long id, Event event) {
        User user = getUserById(id);
        user.getAttendingEvents().add(event);
        int attendeesCount = event.getUsersAttending() + 1;
        return userRepository.save(user);
    }

    public User removeFollowOrganization(Long id, Organisation org) {
        User user = getUserById(id);
        user.getFollowedOrganisations().remove(org);
        int followCount = org.getUsersFollowing() + 1;
        return userRepository.save(user);
    }

    public User removeAttendEvent(Long id, Event event) {
        User user = getUserById(id);
        user.getAttendingEvents().remove(event);
        int attendeesCount = event.getUsersAttending() - 1;
        return userRepository.save(user);
    }

    public CallbackPreference addOrUpdateCallbackPreference(
            Long userId, CallbackPreference callback) {
        User user = getUserById(userId);

        Optional<CallbackPreference> existing =
                user.getCallbackPreferences().stream()
                        .filter(c -> c.getDay() == callback.getDay())
                        .findFirst();

        CallbackPreference savedPreference;

        if (existing.isPresent()) {
            existing.get().setTime(callback.getTime());
            existing.get().setRepeat(callback.getRepeat());
            savedPreference = existing.get();
        } else {
            callback.setUser(user);
            user.getCallbackPreferences().add(callback);
            savedPreference = callback;
        }
        userRepository.save(user);
        return savedPreference;
    }

    public User removeCallbackPreference(Long userId, DayOfWeekType day) {
        User user = getUserById(userId);

        user.getCallbackPreferences().removeIf(c -> c.getDay() == day);

        return userRepository.save(user);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
