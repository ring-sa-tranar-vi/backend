package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.calendarEventDtos.CalendarEventDto;
import dev.salt.Ring20.service.model.CalendarEvent;

public class CalendarMapper {
    public static CalendarEventDto toDto(CalendarEvent calendarEvent) {
        return new CalendarEventDto(
                calendarEvent.getId(),
                calendarEvent.getType(),
                calendarEvent.getTitle(),
                calendarEvent.getDescription(),
                calendarEvent.getTime(),
                calendarEvent.isCompleted());
    }
}
