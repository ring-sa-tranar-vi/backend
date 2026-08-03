package dev.salt.Ring20.dto.calendarEventDtos;

import java.time.LocalDateTime;
// TODO exist calendarEventType enum, why not use it?
public record CalendarEventDto(
        String id,
        String type,
        String title,
        String description,
        LocalDateTime time,
        boolean completed) {}
