package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.enums.UserRole;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
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
    @Mock private OrganisationRepository organisationRepository;
    @Mock private EventRepository eventRepository;

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

        User updated =
                userService.updateUserPreferencesByClerkId(
                        "clerk_1", "  Updated  ", 4, "new", 7L, "Stockholm", false);

        assertEquals("Updated", updated.getName());
        assertEquals(4, updated.getIntensityLevel());
        assertEquals("new", updated.getContext());
        assertEquals(7L, updated.getTrainerId());
        assertEquals("Stockholm", updated.getCity());

        verify(userRepository).save(user);
    }

    @Test
    void updateUserPreferencesByClerkIdRejectsMissingTrainer() {
        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                userService.updateUserPreferencesByClerkId(
                                        "clerk_1", "Name", 3, "context", null, "Stockholm", false));

        assertEquals("Trainer is required", ex.getMessage());
    }

    @Test
    void updateUserPreferencesRejectsUnknownTrainer() {
        when(trainerRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                userService.updateUserPreferencesByClerkId(
                                        "clerk_1", "Name", 3, "context", 999L, "Stockholm", false));

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
}
