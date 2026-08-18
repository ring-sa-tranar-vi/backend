package dev.salt.Ring20.dto.calendarEvent;

import dev.salt.Ring20.entity.enums.CallBackStatus;
import java.time.LocalDateTime;

public record CalendarEventDto(
        String id,
        Long scheduledCallId,
        CallBackStatus callBackStatus,
        String type,
        String title,
        String description,
        LocalDateTime time,
        boolean completed) {}
