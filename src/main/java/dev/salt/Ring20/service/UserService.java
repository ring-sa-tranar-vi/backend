package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.entity.enums.DayOfWeekType;
import dev.salt.Ring20.entity.enums.UserRole;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.TrainerRepository;
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
    private final TrainerRepository trainerRepository;
    private final OrganisationRepository organisationRepository;
    private final EventRepository eventRepository;

    public UserService(
            UserRepository userRepository,
            TrainerRepository trainerRepository,
            OrganisationRepository organisationRepository,
            EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.organisationRepository = organisationRepository;
        this.eventRepository = eventRepository;
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
        //TODO: constant
        return userRepository.save(new User(displayName, STARTING_INTENSITY, "", clerkId));
    }

    public void setFcmToken(Long id, String token) {
        User user = getUserById(id);
        user.setFcmToken(token);
        userRepository.save(user);
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
            String city,
            boolean onboarding) {
        if (trainerId == null) {
            throw new IllegalArgumentException("Trainer is required");
        }
        if (!trainerRepository.existsById(trainerId)) {
            throw new IllegalArgumentException("Trainer does not exist with id: " + trainerId);
        }

        User user = getByClerkIdOrThrow(clerkId);

        user.setName(sanitizeDisplayName(name));
        user.setIntensityLevel(intensityLevel);
        user.setContext(context);
        user.setTrainerId(trainerId);
        user.setCity(city);
        user.setOnboarding(onboarding);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }
    //TODO: fix typo
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
    public Organisation addFollowOrganization(Long userId, Long orgId) {
        User user = getUserById(userId);
        Organisation org =
                organisationRepository
                        .findByIdWithEvents(orgId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Organisation not found with id: " + orgId));

        boolean alreadyFollowing =
                user.getFollowedOrganisations().stream().anyMatch(o -> o.getId().equals(orgId));

        if (!alreadyFollowing) {
            user.getFollowedOrganisations().add(org);
            //TODO: Magic number ?
            org.setUsersFollowing(org.getUsersFollowing() + 1);
        }

        return org;
    }

    @Transactional
    public void removeFollowOrganization(Long userId, Long orgId) {
        User user = getUserById(userId);

        boolean removed =
                user.getFollowedOrganisations().removeIf(org -> org.getId().equals(orgId));

        if (!removed) {
            throw new NoSuchElementException("User is not following organisation: " + orgId);
        }

        Organisation org =
                organisationRepository
                        .findById(orgId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Organisation not found with id: " + orgId));

        org.setUsersFollowing(Math.max(0, org.getUsersFollowing() - 1));
    }

    @Transactional
    public Event addAttendEvent(Long userId, Long eventId) {
        User user = getUserById(userId);

        Event event =
                eventRepository
                        .findByIdWithOrganisation(eventId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Event not found with id: " + eventId));

        boolean alreadyAttending =
                user.getAttendingEvents().stream().anyMatch(e -> e.getId().equals(eventId));

        if (!alreadyAttending) {
            user.getAttendingEvents().add(event);
            //TODO: magic number?
            event.setUsersAttending(event.getUsersAttending() + 1);
        }

        return event;
    }

    @Transactional
    public void removeAttendEvent(Long userId, Long eventId) {
        User user = getUserById(userId);

        boolean removed =
                user.getAttendingEvents().removeIf(event -> event.getId().equals(eventId));

        if (!removed) {
            throw new NoSuchElementException("User is not attending event: " + eventId);
        }

        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Event not found with id: " + eventId));

        event.setUsersAttending(Math.max(0, event.getUsersAttending() - 1));
    }

    public List<CallbackPreference> getCallbackPreferences(Long userId) {
        User user =
                userRepository
                        .findByIdWithCallbackPreferences(userId)
                        .orElseThrow(() -> new NoSuchElementException("User not found"));

        return user.getCallbackPreferences();
    }

    @Transactional
    public CallbackPreference addOrUpdateCallbackPreference(
            Long userId, CallbackPreference callback) {
        User user = getUserById(userId);

        Optional<CallbackPreference> existing =
                user.getCallbackPreferences().stream()
                        .filter(c -> c.getDay() == callback.getDay())
                        .findFirst();

        if (existing.isPresent()) {
            CallbackPreference preference = existing.get();
            preference.setTime(callback.getTime());
            preference.setRepeat(callback.getRepeat());

            return preference;
        }

        callback.setUser(user);
        user.getCallbackPreferences().add(callback);

        return callback;
    }

    @Transactional
    public void removeCallbackPreference(Long userId, DayOfWeekType day) {
        User user = getUserById(userId);

        boolean removed = user.getCallbackPreferences().removeIf(c -> c.getDay() == day);

        if (!removed) {
            throw new NoSuchElementException("No callback preference found for day: " + day);
        }
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
