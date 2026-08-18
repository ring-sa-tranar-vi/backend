package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.calendarEvent.CalendarEventDto;
import dev.salt.Ring20.service.data.CalendarEventData;

public class CalendarMapper {
    public static CalendarEventDto toDto(CalendarEventData calendarEvent) {
        return new CalendarEventDto(
                calendarEvent.id(),
                calendarEvent.scheduledCallId(),
                calendarEvent.callBackStatus(),
                calendarEvent.type(),
                calendarEvent.title(),
                calendarEvent.description(),
                calendarEvent.time(),
                calendarEvent.completed());
    }
}
