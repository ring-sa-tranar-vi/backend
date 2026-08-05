package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.UpdateCompanyEventDto;
import dev.salt.Ring20.dto.company.CompanyEventDto;
import dev.salt.Ring20.dto.company.CreateCompanyEventDto;
import dev.salt.Ring20.dto.eventDtos.EventCreateRequestDto;
import dev.salt.Ring20.dto.eventDtos.EventResponseDto;
import dev.salt.Ring20.dto.eventDtos.EventUpdateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.enums.EventType;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class EventMapper {
    public static CompanyEventDto toCompanyEventDto(Event event) {
        return new CompanyEventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getCity(),
                event.getVenue(),
                event.getUsersAttending(),
                event.getEventType() == null ? null : event.getEventType().name());
    }

    public static EventResponseDto toEventResponseDto(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getOrganisation().getId(),
                event.getCity(),
                event.getVenue(),
                event.getEventType());
    }

    public static Event toEvent(EventCreateRequestDto dto) {
        Event event = new Event();
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setTime(dto.time());
        event.setCity(dto.city());
        event.setVenue(dto.venue());
        event.setEventType(dto.eventType());
        return event;
    }
    public static Event toEvent(CreateCompanyEventDto dto) {
        Event event = new Event();
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setTime(dto.time());
        event.setCity(dto.city());
        event.setVenue(dto.venue());
        event.setEventType(parseEventType(dto.eventType()));
        return event;
    }
    public static Event toEvent(EventUpdateRequestDto dto){
        Event event = new Event();
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setTime(dto.time());
        event.setCity(dto.city());
        event.setVenue(dto.venue());
        event.setEventType(dto.eventType());
        return event;
    }
    public static Event toEvent(UpdateCompanyEventDto dto) {
        Event event = new Event();
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setTime(dto.time());
        event.setCity(dto.city());
        event.setVenue(dto.venue());
        event.setEventType(parseEventType(dto.eventType()));
        return event;
    }
    private static EventType parseEventType(String eventType) {
        try {
            return EventType.valueOf(eventType);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    BAD_REQUEST, "Unsupported eventType: " + eventType, exception);
        }
    }
}
