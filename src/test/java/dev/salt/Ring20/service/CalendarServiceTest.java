package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.CalendarEventDto;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ActivityLogRepository activityLogRepository;
    @Mock
    private CallbackPreferenceRepository callbackPreferenceRepository;
    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private CalendarService calendarService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", 1, "Context", "clerk_123");
        testUser.setId(1L);
    }

    @Test
    void getMonthlyCalendar_shouldAggregateAndSortAllEvents() {
        int year = 2026;
        int month = 8;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ActivityLog workoutLog = new ActivityLog();
        workoutLog.setId(100L);
        workoutLog.setWorkoutId(10L);
        workoutLog.setCompletedAt(LocalDateTime.of(2026, 8, 15, 10, 0));

        Workout workout = new Workout();
        workout.setName("Test Workout");

        when(activityLogRepository.findByUserIdAndStatusAndCompletedAtBetween(
                eq(1L), eq("COMPLETED"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(workoutLog));
        when(workoutRepository.findById(10L)).thenReturn(Optional.of(workout));

        Event event = new Event();
        event.setId(200L);
        event.setName("Yoga Event");
        event.setTime(LocalDateTime.of(2026, 8, 20, 18, 0));
        testUser.getAttendingEvents().add(event);

        CallbackPreference callPref = new CallbackPreference();
        callPref.setId(300L);
        callPref.setDay(DayOfWeekType.MONDAY);
        callPref.setTime(LocalTime.of(15, 0));

        when(callbackPreferenceRepository.findByUserId(1L)).thenReturn(List.of(callPref));

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, year, month);

        assertEquals(7, result.size());
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                    result.get(i).time().isBefore(result.get(i + 1).time()) ||
                            result.get(i).time().isEqual(result.get(i + 1).time()),
                    "The list is not sorted correctly!"
            );
        }

        CalendarEventDto eventDto = result.stream()
                .filter(e -> e.type().equals("EVENT"))
                .findFirst()
                .orElseThrow();
        assertEquals("Yoga Event", eventDto.title());
        assertEquals("EVENT-200", eventDto.id());
    }

    @Test
    void getMonthlyCalendar_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                calendarService.getMonthlyCalendar(99L, 2026, 8)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getMonthlyCalendar_shouldFilterEventsOutsideOfRequestedMonth() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Event validEvent = new Event();
        validEvent.setId(1L);
        validEvent.setName("August Event");
        validEvent.setTime(LocalDateTime.of(2026, 8, 15, 12, 0));

        Event invalidEvent = new Event();
        invalidEvent.setId(2L);
        invalidEvent.setName("July Event");
        invalidEvent.setTime(LocalDateTime.of(2026, 7, 31, 23, 59));

        testUser.getAttendingEvents().addAll(List.of(validEvent, invalidEvent));

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size(), "Should only show 1 event because the other was in July");
        assertEquals("EVENT-1", result.get(0).id());
        assertEquals("August Event", result.get(0).title());
    }

    @Test
    void getMonthlyCalendar_shouldFormatEventDescriptionAndLocationCorrectly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Event event = new Event();
        event.setId(10L);
        event.setName("Workshop");
        event.setDescription("A fun workshop");
        event.setVenue("Salt HQ");
        event.setCity("Stockholm");
        event.setTime(LocalDateTime.of(2026, 8, 5, 10, 0));

        testUser.getAttendingEvents().add(event);

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size());
        assertEquals("A fun workshop - Salt HQ, Stockholm", result.get(0).description(),
                "The description was not formatted correctly!");
    }

    @Test
    void getMonthlyCalendar_shouldReturnEmptyList_whenNoDataExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUserIdAndStatusAndCompletedAtBetween(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(callbackPreferenceRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertTrue(result.isEmpty(), "The calendar should be completely empty");
    }

    @Test
    void getMonthlyCalendar_shouldHandleLeapYearsForScheduledCalls() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        CallbackPreference callPref = new CallbackPreference();
        callPref.setId(1L);
        callPref.setDay(DayOfWeekType.THURSDAY);
        callPref.setTime(LocalTime.of(10, 0));

        when(callbackPreferenceRepository.findByUserId(1L)).thenReturn(List.of(callPref));

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, 2024, 2);

        assertEquals(5, result.size());
        assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), result.get(4).time());
    }

    @Test
    void getMonthlyCalendar_shouldUseFallbackNameForDeletedWorkout() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ActivityLog workoutLog = new ActivityLog();
        workoutLog.setId(50L);
        workoutLog.setWorkoutId(999L);
        workoutLog.setCompletedAt(LocalDateTime.of(2026, 8, 10, 14, 0));

        when(activityLogRepository.findByUserIdAndStatusAndCompletedAtBetween(
                eq(1L), eq("COMPLETED"), any(), any())).thenReturn(List.of(workoutLog));

        when(workoutRepository.findById(999L)).thenReturn(Optional.empty());

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size());
        assertEquals("Deleted training", result.get(0).title());
    }

    @Test
    void getMonthlyCalendar_shouldFormatEventWithoutVenue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Event event = new Event();
        event.setId(15L);
        event.setName("Outdoor Run");
        event.setCity("Gothenburg");
        event.setTime(LocalDateTime.of(2026, 8, 12, 10, 0));

        testUser.getAttendingEvents().add(event);

        List<CalendarEventDto> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size());
        assertEquals("Gothenburg", result.get(0).description());
    }
}