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

    private final UserRepository userRepository;
    private static final int STARTING_INTENSITY = 2;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAdmin(String clerkID) {

        return "ADMIN".equals(getByClerkIdOrThrow(clerkID).getRole());
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

    public Optional<User> findByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId);
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
        User user = getUserById(id);
        return user.getFollowedOrganisations();
    }

    public List<Event> getUserEventsById(Long id) {
        User user = getUserById(id);
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
        if (!user.getAttendingEvents().contains(event)) {
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
        if (user.getAttendingEvents().remove(event)) {
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
