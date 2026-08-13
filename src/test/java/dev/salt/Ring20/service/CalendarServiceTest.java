package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.entity.enums.DayOfWeekType;
import dev.salt.Ring20.entity.enums.CallBackStatus;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.ScheduledCallRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import dev.salt.Ring20.service.data.CalendarEventData;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ActivityLogRepository activityLogRepository;
    @Mock private CallbackPreferenceRepository callbackPreferenceRepository;
    @Mock private ScheduledCallRepository scheduledCallRepository;
    @Mock private WorkoutRepository workoutRepository;

    @InjectMocks private CalendarService calendarService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", 1, "Context", "clerk_123");
        testUser.setId(1L);
        testUser.setTimeZone("UTC");
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
                        eq(1L),
                        eq("COMPLETED"),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(workoutLog));
        when(workoutRepository.findById(10L)).thenReturn(Optional.of(workout));

        Event event = new Event();
        event.setId(200L);
        event.setName("Yoga Event");
        event.setTime(LocalDateTime.of(2026, 8, 20, 18, 0));
        testUser.getAttendingEvents().add(event);

        ScheduledCall call1 = new ScheduledCall();
        call1.setId(300L);
        call1.setTargetTime(LocalDateTime.of(2026, 8, 3, 15, 0).toInstant(ZoneOffset.UTC));
        call1.setCallBackStatus(CallBackStatus.PENDING);

        ScheduledCall call2 = new ScheduledCall();
        call2.setId(301L);
        call2.setTargetTime(LocalDateTime.of(2026, 8, 10, 15, 0).toInstant(ZoneOffset.UTC));
        call2.setCallBackStatus(CallBackStatus.TRIGGERED);

        ScheduledCall call3 = new ScheduledCall();
        call3.setId(302L);
        call3.setTargetTime(LocalDateTime.of(2026, 8, 17, 15, 0).toInstant(ZoneOffset.UTC));
        call3.setCallBackStatus(CallBackStatus.RECEIVED);

        ScheduledCall call4 = new ScheduledCall();
        call4.setId(303L);
        call4.setTargetTime(LocalDateTime.of(2026, 8, 24, 15, 0).toInstant(ZoneOffset.UTC));
        call4.setCallBackStatus(CallBackStatus.COMPLETED);

        ScheduledCall call5 = new ScheduledCall();
        call5.setId(304L);
        call5.setTargetTime(LocalDateTime.of(2026, 8, 31, 15, 0).toInstant(ZoneOffset.UTC));
        call5.setCallBackStatus(CallBackStatus.CANCELLED);

        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of(call1, call2, call3, call4, call5));

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, year, month);

        assertEquals(7, result.size());
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                    result.get(i).time().isBefore(result.get(i + 1).time())
                            || result.get(i).time().isEqual(result.get(i + 1).time()),
                    "The list is not sorted correctly!");
        }

        CalendarEventData eventDto =
                result.stream().filter(e -> e.type().equals("EVENT")).findFirst().orElseThrow();
        assertEquals("Yoga Event", eventDto.title());
        assertEquals("EVENT-200", eventDto.id());

        CalendarEventData callDto =
            result.stream().filter(e -> e.type().equals("CALL")).findFirst().orElseThrow();
        assertEquals(300L, callDto.scheduledCallId());
        assertEquals(CallBackStatus.PENDING, callDto.callBackStatus());
        assertEquals("300", callDto.id());
    }

    @Test
    void getMonthlyCalendar_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> calendarService.getMonthlyCalendar(99L, 2026, 8));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getMonthlyCalendar_shouldFilterEventsOutsideOfRequestedMonth() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of());

        Event validEvent = new Event();
        validEvent.setId(1L);
        validEvent.setName("August Event");
        validEvent.setTime(LocalDateTime.of(2026, 8, 15, 12, 0));

        Event invalidEvent = new Event();
        invalidEvent.setId(2L);
        invalidEvent.setName("July Event");
        invalidEvent.setTime(LocalDateTime.of(2026, 7, 31, 23, 59));

        testUser.getAttendingEvents().addAll(List.of(validEvent, invalidEvent));

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size(), "Should only show 1 event because the other was in July");
        assertEquals("EVENT-1", result.getFirst().id());
        assertEquals("August Event", result.getFirst().title());
    }

    @Test
    void getMonthlyCalendar_shouldKeepAttendedEventsAfterTheyHaveEnded() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of());

        Event pastEvent = new Event();
        pastEvent.setId(3L);
        pastEvent.setName("Completed Community Event");
        pastEvent.setTime(LocalDateTime.of(2020, 8, 15, 12, 0));
        testUser.getAttendingEvents().add(pastEvent);

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2020, 8);

        assertEquals(1, result.size());
        assertEquals("EVENT-3", result.getFirst().id());
        assertEquals("Completed Community Event", result.getFirst().title());
        assertTrue(result.getFirst().completed());
    }

    @Test
    void getMonthlyCalendar_shouldFormatEventDescriptionAndLocationCorrectly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of());

        Event event = new Event();
        event.setId(10L);
        event.setName("Workshop");
        event.setDescription("A fun workshop");
        event.setVenue("Salt HQ");
        event.setCity("Stockholm");
        event.setTime(LocalDateTime.of(2026, 8, 5, 10, 0));

        testUser.getAttendingEvents().add(event);

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size());
        assertEquals(
                "A fun workshop - Salt HQ, Stockholm",
                result.getFirst().description(),
                "The description was not formatted correctly!");
    }

    @Test
    void getMonthlyCalendar_shouldReturnEmptyList_whenNoDataExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUserIdAndStatusAndCompletedAtBetween(
                        any(), any(), any(), any()))
                .thenReturn(List.of());
        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of());

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertTrue(result.isEmpty(), "The calendar should be completely empty");
    }

    @Test
    void getMonthlyCalendar_shouldHandleLeapYearsForScheduledCalls() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ScheduledCall call1 = new ScheduledCall();
        call1.setId(1L);
        call1.setTargetTime(LocalDateTime.of(2024, 2, 1, 10, 0).toInstant(ZoneOffset.UTC));
        call1.setCallBackStatus(CallBackStatus.PENDING);

        ScheduledCall call2 = new ScheduledCall();
        call2.setId(2L);
        call2.setTargetTime(LocalDateTime.of(2024, 2, 8, 10, 0).toInstant(ZoneOffset.UTC));
        call2.setCallBackStatus(CallBackStatus.PENDING);

        ScheduledCall call3 = new ScheduledCall();
        call3.setId(3L);
        call3.setTargetTime(LocalDateTime.of(2024, 2, 15, 10, 0).toInstant(ZoneOffset.UTC));
        call3.setCallBackStatus(CallBackStatus.PENDING);

        ScheduledCall call4 = new ScheduledCall();
        call4.setId(4L);
        call4.setTargetTime(LocalDateTime.of(2024, 2, 22, 10, 0).toInstant(ZoneOffset.UTC));
        call4.setCallBackStatus(CallBackStatus.PENDING);

        ScheduledCall call5 = new ScheduledCall();
        call5.setId(5L);
        call5.setTargetTime(LocalDateTime.of(2024, 2, 29, 10, 0).toInstant(ZoneOffset.UTC));
        call5.setCallBackStatus(CallBackStatus.PENDING);

        when(scheduledCallRepository.findByUserId(1L))
            .thenReturn(List.of(call1, call2, call3, call4, call5));

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2024, 2);

        assertEquals(5, result.size());
        assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), result.get(4).time());
    }

    @Test
    void getMonthlyCalendar_shouldUseFallbackNameForDeletedWorkout() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of());

        ActivityLog workoutLog = new ActivityLog();
        workoutLog.setId(50L);
        workoutLog.setWorkoutId(999L);
        workoutLog.setCompletedAt(LocalDateTime.of(2026, 8, 10, 14, 0));

        when(activityLogRepository.findByUserIdAndStatusAndCompletedAtBetween(
                        eq(1L), eq("COMPLETED"), any(), any()))
                .thenReturn(List.of(workoutLog));

        when(workoutRepository.findById(999L)).thenReturn(Optional.empty());

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size());
        assertEquals("Deleted training", result.getFirst().title());
    }

    @Test
    void getMonthlyCalendar_shouldFormatEventWithoutVenue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(scheduledCallRepository.findByUserId(1L)).thenReturn(List.of());

        Event event = new Event();
        event.setId(15L);
        event.setName("Outdoor Run");
        event.setCity("Gothenburg");
        event.setTime(LocalDateTime.of(2026, 8, 12, 10, 0));

        testUser.getAttendingEvents().add(event);

        List<CalendarEventData> result = calendarService.getMonthlyCalendar(1L, 2026, 8);

        assertEquals(1, result.size());
        assertEquals("Gothenburg", result.getFirst().description());
    }
}
