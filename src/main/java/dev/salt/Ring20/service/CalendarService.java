package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.CalendarEventDto;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
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
    public List<CalendarEventDto> getMonthlyCalendar(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CalendarEventDto> calendarEvents = new ArrayList<>();

        calendarEvents.addAll(getWorkouts(userId, startOfMonth, endOfMonth));
        calendarEvents.addAll(getEvents(user, startOfMonth, endOfMonth));
        calendarEvents.addAll(getScheduledCalls(userId, yearMonth));
        calendarEvents.sort(Comparator.comparing(CalendarEventDto::time));

        return calendarEvents;
    }

    private List<CalendarEventDto> getWorkouts(
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

                            return new CalendarEventDto(
                                    "WORKOUT-" + log.getId(),
                                    "WORKOUT",
                                    workoutName,
                                    "Time: "
                                            + (log.getDurationSeconds() != null
                                                    ? log.getDurationSeconds() / 60 + " min"
                                                    : "N/A"),
                                    log.getCompletedAt(),
                                    true);
                        })
                .toList();
    }

    private List<CalendarEventDto> getEvents(User user, LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();

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

                            return new CalendarEventDto(
                                    "EVENT-" + event.getId(),
                                    "EVENT",
                                    event.getName(),
                                    fullDescription,
                                    event.getTime(),
                                    event.getTime().isBefore(now));
                        })
                .toList();
    }

    private List<CalendarEventDto> getScheduledCalls(Long userId, YearMonth yearMonth) {
        List<CallbackPreference> preferences = callbackPreferenceRepository.findByUserId(userId);
        List<CalendarEventDto> callEvents = new ArrayList<>();

        LocalDate currentDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        LocalDateTime now = LocalDateTime.now();

        for (CallbackPreference pref : preferences) {
            DayOfWeek targetDay = DayOfWeek.valueOf(pref.getDay().name());

            LocalDate dateIterator = currentDate;
            while (!dateIterator.isAfter(endDate)) {
                if (dateIterator.getDayOfWeek() == targetDay) {
                    LocalDateTime callTime = LocalDateTime.of(dateIterator, pref.getTime());

                    callEvents.add(
                            new CalendarEventDto(
                                    "CALL-" + pref.getId() + "-" + dateIterator.toString(),
                                    "CALL",
                                    "Trainer Call",
                                    "Trainer call",
                                    callTime,
                                    callTime.isBefore(now)));
                }
                dateIterator = dateIterator.plusDays(1);
            }
        }

        return callEvents;
    }
}
