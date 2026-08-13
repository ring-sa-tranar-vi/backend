package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.user.UserTimeZoneDto;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.entity.enums.DayOfWeekType;
import dev.salt.Ring20.entity.enums.UserRole;
import dev.salt.Ring20.repository.*;
import jakarta.transaction.Transactional;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String DEFAULT_DISPLAY_NAME = "No name entered";
    private static final int STARTING_INTENSITY = 2;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final OrganizationRepository organizationRepository;
    private final EventRepository eventRepository;
    private final CallbackPreferenceRepository callbackPreferenceRepository;
    private final ScheduledCallService scheduledCallService;
    private final ScheduledCallRepository scheduledCallRepository;

    public UserService(
            UserRepository userRepository,
            TrainerRepository trainerRepository,
            OrganizationRepository organizationRepository,
            EventRepository eventRepository,
            CallbackPreferenceRepository callbackPreferenceRepository,
            ScheduledCallService scheduledCallService,
            ScheduledCallRepository scheduledCallRepository) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.organizationRepository = organizationRepository;
        this.eventRepository = eventRepository;
        this.callbackPreferenceRepository = callbackPreferenceRepository;
        this.scheduledCallService = scheduledCallService;
        this.scheduledCallRepository = scheduledCallRepository;
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

    public void setFcmToken(Long id, String newToken) {
        User user = getUserById(id);
        if (newToken.equals(user.getFcmToken())) {
            return;
        }

        user.setFcmToken(newToken);
        userRepository.save(user);

        int updated =
                scheduledCallRepository.updateFcmTokenForFuturePendingCalls(
                        id,
                        newToken,
                        Instant.now());

        log.info(
                "Updated FCM token for user={}, updated {} future pending calls",
                id,
                updated);
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

    public List<Organization> getUserOrgsById(Long id) {
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
    public Organization addFollowOrganization(Long userId, Long orgId) {
        User user = getUserById(userId);
        Organization org =
                organizationRepository
                        .findByIdWithEvents(orgId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Organisation not found with id: " + orgId));

        boolean alreadyFollowing =
                user.getFollowedOrganisations().stream().anyMatch(o -> o.getId().equals(orgId));

        if (!alreadyFollowing) {
            user.getFollowedOrganisations().add(org);
            org.setUsersFollowing(org.getUsersFollowing() + 1);
        }

        return org;
    }

    @Transactional
    public void removeFollowOrganization(Long userId, Long orgId) {
        User user = getUserById(userId);

        boolean removed =
                user.getFollowedOrganisations().removeIf(org -> org.getId().equals(orgId));

        if (removed) {
            Organization org =
                    organizationRepository
                            .findById(orgId)
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "Organisation not found with id: " + orgId));

            org.setUsersFollowing(Math.max(0, org.getUsersFollowing() - 1));
        }
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
            scheduledCallService.resetCallsForPreference(preference);
            return preference;
        }

        callback.setUser(user);
        CallbackPreference savedCallback = callbackPreferenceRepository.saveAndFlush(callback);
        user.getCallbackPreferences().add(savedCallback);
        scheduledCallService.ensureRollingCalls(savedCallback);
        return savedCallback;
    }

    @Transactional
    public void removeCallbackPreference(Long userId, DayOfWeekType day) {
        User user = getUserById(userId);

        CallbackPreference pref =
                user.getCallbackPreferences().stream()
                        .filter(c -> c.getDay() == day)
                        .findFirst()
                        .orElseThrow(
                                () -> new NoSuchElementException("No callback preference found"));

        scheduledCallService.cancelFutureCallsForOnePreference(pref);
        scheduledCallService.detachHistoricalCallsFromPreference(pref.getId());
        user.getCallbackPreferences().remove(pref);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public void removeUser(String clerkId) {
        User user = getUserById(getInternalUserId(clerkId));
        userRepository.delete(user);
    }

    @Transactional
    public String updateUserTimeZone(String clerkId, String timeZone) {
        User user = getUserById(getInternalUserId(clerkId));
        try {
            ZoneId.of(timeZone);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(
                    "Invalid time zone: " + timeZone);
        }

        user.setTimeZone(timeZone);
        userRepository.save(user);

        return user.getTimeZone();
    }
}
