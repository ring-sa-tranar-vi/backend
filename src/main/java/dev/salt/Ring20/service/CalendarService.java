package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.entity.enums.RepeatType;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import dev.salt.Ring20.service.data.CalendarEventData;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final CallbackPreferenceRepository callbackPreferenceRepository;
    private final WorkoutRepository workoutRepository;

    @Transactional(readOnly = true)
    public List<CalendarEventData> getMonthlyCalendar(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CalendarEventData> calendarEvents = new ArrayList<>();

        calendarEvents.addAll(getWorkouts(userId, startOfMonth, endOfMonth));
        calendarEvents.addAll(getEvents(user, startOfMonth, endOfMonth));
        calendarEvents.addAll(getScheduledCalls(userId, yearMonth));
        calendarEvents.sort(Comparator.comparing(CalendarEventData::time));

        return calendarEvents;
    }

    private List<CalendarEventData> getWorkouts(
            Long userId, LocalDateTime start, LocalDateTime end) {
        List<ActivityLog> logs =
                activityLogRepository.findByUserIdAndStatusAndCompletedAtBetween(
                        userId, "COMPLETED", start, end);
        return logs.stream()
                .map(
                        log -> {
                            String workoutName =
                                    workoutRepository
                                            .findById(log.getWorkoutId())
                                            .map(Workout::getName)
                                            .orElse("Deleted training");

                            return createWorkoutCalendarEvent(log, workoutName);
                        })
                .toList();
    }

    private List<CalendarEventData> getEvents(User user, LocalDateTime start, LocalDateTime end) {

        return user.getAttendingEvents().stream()
                .filter(event -> !event.getTime().isBefore(start) && !event.getTime().isAfter(end))
                .map(
                        event -> {
                            String baseDesc =
                                    event.getDescription() != null ? event.getDescription() : "";
                            String location =
                                    (event.getVenue() != null ? event.getVenue() + ", " : "")
                                            + event.getCity();
                            String fullDescription =
                                    baseDesc.isEmpty() ? location : baseDesc + " - " + location;

                            return createEventCalenderEvent(event, fullDescription);
                        })
                .toList();
    }

    private List<CalendarEventData> getScheduledCalls(Long userId, YearMonth yearMonth) {
        List<CallbackPreference> preferences = callbackPreferenceRepository.findByUserId(userId);
        List<CalendarEventData> callEvents = new ArrayList<>();

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        LocalDateTime now = LocalDateTime.now();

        for (CallbackPreference pref : preferences){

            DayOfWeek targetDay = DayOfWeek.valueOf(pref.getDay().name());
            LocalDate date = start;

            while (!date.isAfter(end)) {

                if (date.getDayOfWeek() != targetDay) {
                    date = date.plusDays(1);
                    continue;
                }

                LocalDateTime callTime = LocalDateTime.of(date, pref.getTime());

                if (pref.getRepeat() == RepeatType.NEVER && callTime.isBefore(now)) {
                    date = date.plusDays(1);
                    continue;
                }


                callEvents.add(createCallCalendarEvent(pref, callTime, date));


                if (pref.getRepeat() == RepeatType.NEVER) {
                    break;
                }

                date = date.plusDays(1);
            }
        }

        return callEvents;
    }

    private CalendarEventData createCallCalendarEvent(
            CallbackPreference pref, LocalDateTime callTime, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        return createCalenderEventData(
                "CALL-" + pref.getId() + "-" + date,
                "CALL",
                "Trainer Call",
                "Trainer call",
                callTime,
                callTime.isBefore(now));
    }

    private CalendarEventData createWorkoutCalendarEvent(ActivityLog log, String workoutName) {
        return createCalenderEventData(
                "WORKOUT-" + log.getId(),
                "WORKOUT",
                workoutName,
                "Time: "
                        + (log.getDurationSeconds() != null
                                ? log.getDurationSeconds() / 60 + " min"
                                : "N/A"),
                log.getCompletedAt(),
                true);
    }

    private CalendarEventData createEventCalenderEvent(Event event, String fullDescription) {
        LocalDateTime now = LocalDateTime.now();
        return createCalenderEventData(
                "EVENT-" + event.getId(),
                "EVENT",
                event.getName(),
                fullDescription,
                event.getTime(),
                event.getTime().isBefore(now));
    }

    private CalendarEventData createCalenderEventData(
            String id,
            String type,
            String title,
            String description,
            LocalDateTime time,
            boolean completed) {

        return new CalendarEventData(id, type, title, description, time, completed);
    }
}
