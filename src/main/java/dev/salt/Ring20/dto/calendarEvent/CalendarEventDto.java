package dev.salt.Ring20.dto.calendarEvent;

import java.time.LocalDateTime;

public record CalendarEventDto(
        String id,
        String type,
        String title,
        String description,
        LocalDateTime time,
        boolean completed) {}
