package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.enums.UserRole;
import dev.salt.Ring20.entity.enums.DayOfWeekType;
import dev.salt.Ring20.entity.enums.RepeatType;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.OrganizationRepository;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private EventRepository eventRepository;
    @Mock private CallbackPreferenceRepository callbackPreferenceRepository;
    @Mock private ScheduledCallService scheduledCallService;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Jane Doe", 2, "context", "clerk_1");
        user.setId(1L);
        user.setRole(UserRole.USER);
        user.setTrainerId(2L);
    }

    @Test
    void isAdminReturnsTrueForAdminRole() {
        user.setRole(UserRole.ADMIN);
        when(userRepository.findByClerkId("clerk_1")).thenReturn(Optional.of(user));

        assertTrue(userService.isAdmin("clerk_1"));
    }

    @Test
    void createUserCreatesNewUserWithProvidedName() {
        when(userRepository.findByClerkId("clerk_2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser("clerk_2", "  New User  ");

        assertEquals("New User", created.getName());
        assertEquals(UserRole.USER, created.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserUsesDefaultNameWhenBlank() {
        when(userRepository.findByClerkId("clerk_3")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser("clerk_3", "   ");

        assertEquals("No name entered", created.getName());
    }

    @Test
    void getByClerkIdOrThrowThrowsWhenMissing() {
        when(userRepository.findByClerkId("missing")).thenReturn(Optional.empty());

        NoSuchElementException ex =
                assertThrows(
                        NoSuchElementException.class,
                        () -> userService.getByClerkIdOrThrow("missing"));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateUserPreferencesByClerkIdUpdatesAndSaves() {
        when(userRepository.findByClerkId("clerk_1")).thenReturn(Optional.of(user));

        when(trainerRepository.existsById(7L)).thenReturn(true);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User preferences = new User("  Updated  ", 4, "new", "clerk_1");
        preferences.setTrainerId(7L);
        preferences.setCity("Stockholm");
        preferences.setOnboarding(false);

        User updated =
                userService.updateUserPreferencesByClerkId(
                        "clerk_1",
                        preferences.getName(),
                        preferences.getIntensityLevel(),
                        preferences.getContext(),
                        preferences.getTrainerId(),
                        preferences.getCity(),
                        preferences.isOnboarding());

        assertEquals("Updated", updated.getName());
        assertEquals(4, updated.getIntensityLevel());
        assertEquals("new", updated.getContext());
        assertEquals(7L, updated.getTrainerId());
        assertEquals("Stockholm", updated.getCity());

        verify(userRepository).save(user);
    }

    @Test
    void updateUserPreferencesByClerkIdRejectsMissingTrainer() {
        User preferences = new User("Name", 3, "context", "clerk_1");
        preferences.setTrainerId(null);
        preferences.setCity("Stockholm");
        preferences.setOnboarding(false);

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                userService.updateUserPreferencesByClerkId(
                                        "clerk_1",
                                        preferences.getName(),
                                        preferences.getIntensityLevel(),
                                        preferences.getContext(),
                                        preferences.getTrainerId(),
                                        preferences.getCity(),
                                        preferences.isOnboarding()));

        assertEquals("Trainer is required", ex.getMessage());
    }

    @Test
    void updateUserPreferencesRejectsUnknownTrainer() {
        when(trainerRepository.existsById(999L)).thenReturn(false);

        User preferences = new User("Name", 3, "context", "clerk_1");
        preferences.setTrainerId(999L);
        preferences.setCity("Stockholm");
        preferences.setOnboarding(false);

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                userService.updateUserPreferencesByClerkId(
                                        "clerk_1",
                                        preferences.getName(),
                                        preferences.getIntensityLevel(),
                                        preferences.getContext(),
                                        preferences.getTrainerId(),
                                        preferences.getCity(),
                                        preferences.isOnboarding()));

        assertEquals("Trainer does not exist with id: 999", ex.getMessage());
    }

    @Test
    void getUserByIdThrowsWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex =
                assertThrows(NoSuchElementException.class, () -> userService.getUserById(99L));
        assertEquals("User not found with id: 99", ex.getMessage());
    }

    @Test
    void removeAttendEventRemovesEventAndDecrementsCounter() {
        Event event = new Event();
        event.setId(10L);
        event.setUsersAttending(1);

        user.getAttendingEvents().add(event);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        userService.removeAttendEvent(1L, 10L);

        assertTrue(user.getAttendingEvents().isEmpty());
        assertEquals(0, event.getUsersAttending());
    }

    @Test
    void addOrUpdateCallbackPreferenceSavesPreferenceBeforeSchedulingCalls() {
        CallbackPreference preference = new CallbackPreference();
        preference.setDay(DayOfWeekType.MONDAY);
        preference.setTime(LocalTime.of(9, 0));
        preference.setRepeat(RepeatType.WEEKLY);

        CallbackPreference savedPreference = new CallbackPreference();
        savedPreference.setId(42L);
        savedPreference.setDay(DayOfWeekType.MONDAY);
        savedPreference.setTime(LocalTime.of(9, 0));
        savedPreference.setRepeat(RepeatType.WEEKLY);
        savedPreference.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(callbackPreferenceRepository.saveAndFlush(preference)).thenReturn(savedPreference);

        CallbackPreference result = userService.addOrUpdateCallbackPreference(1L, preference);

        assertSame(savedPreference, result);
        verify(callbackPreferenceRepository).saveAndFlush(preference);
        verify(scheduledCallService).ensureRollingCalls(savedPreference);
    }
}
