package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.company.CompanyEventDto;
import dev.salt.Ring20.entity.Event;

public class EventMapper {
    public static CompanyEventDto toCompanyEventDto(Event event){
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
}
