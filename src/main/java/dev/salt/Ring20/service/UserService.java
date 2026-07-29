package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String DEFAULT_DISPLAY_NAME = "No name entered";
    private static final int STARTING_INTENSITY = 2;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAdmin(String clerkId) {
        UserRole role = getUserRole(clerkId);
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }

    public boolean isSuperAdmin(String clerkId) {
        return getUserRole(clerkId) == UserRole.SUPER_ADMIN;
    }

    public UserRole getUserRole(String clerkId) {
        return getByClerkIdOrThrow(clerkId).getRole();
    }

    public Optional<User> findByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId);
    }

    public Long getInternalUserId(String clerkId) {
        return userRepository
                .findByClerkId(clerkId)
                .orElseThrow(() -> new NoSuchElementException("User not found"))
                .getId();
    }

    private String sanitizeDisplayName(String name) {
        return (name == null || name.isBlank()) ? DEFAULT_DISPLAY_NAME : name.trim();
    }

    @Transactional
    public User createUser(String clerkId, String name) {
        String displayName = sanitizeDisplayName(name);
        Optional<User> existing = userRepository.findByClerkId(clerkId);
        if (existing.isPresent()) {
            User user = existing.get();

            if (user.getName() == null
                    || user.getName().isBlank()
                    || DEFAULT_DISPLAY_NAME.equals(user.getName())) {
                user.setName(displayName);
                return userRepository.save(user);
            }

            return user;
        }

        return userRepository.save(new User(displayName, STARTING_INTENSITY, "", clerkId));
    }

    public User getByClerkIdOrThrow(String clerkId) {
        return userRepository
                .findByClerkId(clerkId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Transactional
    public User updateUserPreferencesByClerkId(
            String clerkId,
            String name,
            int intensityLevel,
            String context,
            Long trainerId,
            String city) {
        if (trainerId == null) {
            throw new IllegalArgumentException("Trainer is required");
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
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public List<Organisation> getUserOrgsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }

        return userRepository.findFollowedOrganisationsWithEventsById(id);
    }

    public List<Event> getUserEventsById(Long id) {
        User user =
                userRepository
                        .findByIdWithAttendingEvents(id)
                        .orElseThrow(() -> new NoSuchElementException("User not found"));

        return user.getAttendingEvents();
    }

    @Transactional
    public User addFollowOrganization(Long id, Organisation org) {
        User user = getUserById(id);
        if (!user.getFollowedOrganisations().contains(org)) {
            user.getFollowedOrganisations().add(org);
            org.setUsersFollowing(org.getUsersFollowing() + 1);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User addAttendEvent(Long id, Event event) {
        User user = getUserById(id);
        boolean alreadyAttending =
                user.getAttendingEvents().stream()
                        .anyMatch(attending -> attending.getId().equals(event.getId()));
        if (!alreadyAttending) {
            user.getAttendingEvents().add(event);
            event.setUsersAttending(event.getUsersAttending() + 1);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User removeFollowOrganization(Long id, Organisation org) {
        User user = getUserById(id);
        if (user.getFollowedOrganisations().remove(org)) {
            org.setUsersFollowing(Math.max(0, org.getUsersFollowing() - 1));
        }
        return userRepository.save(user);
    }

    @Transactional
    public User removeAttendEvent(Long id, Event event) {
        User user = getUserById(id);
        boolean removed =
                user.getAttendingEvents()
                        .removeIf(attending -> attending.getId().equals(event.getId()));
        if (removed) {
            event.setUsersAttending(Math.max(0, event.getUsersAttending() - 1));
        }
        return userRepository.save(user);
    }

    @Transactional
    public User addOrUpdateCallbackPreference(Long userId, CallbackPreference callback) {
        User user = getUserById(userId);

        Optional<CallbackPreference> existing =
                user.getCallbackPreferences().stream()
                        .filter(c -> c.getDay() == callback.getDay())
                        .findFirst();

        CallbackPreference savedPreference;

        if (existing.isPresent()) {
            existing.get().setTime(callback.getTime());
            existing.get().setRepeat(callback.getRepeat());
        } else {
            callback.setUser(user);
            user.getCallbackPreferences().add(callback);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User removeCallbackPreference(Long userId, DayOfWeekType day) {
        User user = getUserById(userId);

        user.getCallbackPreferences().removeIf(c -> c.getDay() == day);

        return userRepository.save(user);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
