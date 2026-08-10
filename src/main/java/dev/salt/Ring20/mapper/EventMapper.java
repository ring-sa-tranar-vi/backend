package dev.salt.Ring20.mapper;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import dev.salt.Ring20.dto.admin.AdminCreateEventDto;
import dev.salt.Ring20.dto.company.CompanyEventDto;
import dev.salt.Ring20.dto.company.CreateCompanyEventDto;
import dev.salt.Ring20.dto.company.UpdateCompanyEventDto;
import dev.salt.Ring20.dto.event.EventCreateRequestDto;
import dev.salt.Ring20.dto.event.EventResponseDto;
import dev.salt.Ring20.dto.event.EventUpdateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.entity.enums.EventType;
import java.time.LocalDateTime;
import org.springframework.web.server.ResponseStatusException;

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
        Long organisationId =
                event.getOrganisation() == null ? null : event.getOrganisation().getId();
        return new EventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                organisationId,
                event.getCity(),
                event.getVenue(),
                event.getEventType());
    }

    public static Event toEvent(EventCreateRequestDto dto) {
        return createEvent(
                dto.name(),
                dto.description(),
                dto.time(),
                dto.city(),
                dto.venue(),
                dto.eventType());
    }

    public static Event toEvent(CreateCompanyEventDto dto) {
        return createEvent(
                dto.name(),
                dto.description(),
                dto.time(),
                dto.city(),
                dto.venue(),
                parseEventType(dto.eventType()));
    }

    public static Event toEvent(EventUpdateRequestDto dto) {
        return createEvent(
                dto.name(),
                dto.description(),
                dto.time(),
                dto.city(),
                dto.venue(),
                dto.eventType());
    }

    public static Event toEvent(UpdateCompanyEventDto dto) {
        return createEvent(
                dto.name(),
                dto.description(),
                dto.time(),
                dto.city(),
                dto.venue(),
                parseEventType(dto.eventType()));
    }

    public static Event toEvent(AdminCreateEventDto dto, Organization org) {
        return createEvent(
                dto.name(),
                dto.description(),
                dto.time(),
                org.getOrgCity(),
                null,
                EventType.IN_PERSON);
    }

    //    public static EventResponseDto toEventResponseDto(Event event) {
    //        Long organisationId =
    //                event.getOrganisation() == null ? null : event.getOrganisation().getId();
    //        return new EventResponseDto(
    //                event.getId(),
    //                event.getName(),
    //                event.getDescription(),
    //                event.getTime(),
    //                organisationId,
    //                event.getCity(),
    //                event.getVenue(),
    //                event.getEventType());
    //    }
    private static EventType parseEventType(String eventType) {
        try {
            return EventType.valueOf(eventType);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    BAD_REQUEST, "Unsupported eventType: " + eventType, exception);
        }
    }

    private static Event createEvent(
            String name,
            String description,
            LocalDateTime time,
            String city,
            String venue,
            EventType eventType) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setTime(time);
        event.setCity(city);
        event.setVenue(venue);
        event.setEventType(eventType);
        return event;
    }
}
