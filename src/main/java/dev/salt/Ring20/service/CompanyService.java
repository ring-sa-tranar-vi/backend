package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.CompanyMeDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private static final String COMPANY_ROLE = "COMPANY";
 //TODO: document, what is the difference between this and organisation?

    private final OrganisationService organisationService;
    private final EventService eventService;
    private final UserService userService;

    public CompanyService(
            OrganisationService organisationService,
            EventService eventService,
            UserService userService) {
        this.organisationService = organisationService;
        this.eventService = eventService;
        this.userService = userService;
    }

    public CompanyMeDto getCompanyMe(String clerkId) {
        User user = userService.getByClerkIdOrThrow(clerkId);
        Organisation organisation = getManagedOrganisationForClerkId(clerkId);
        return new CompanyMeDto(
                user.getId(), COMPANY_ROLE, true, organisation.getId(), organisation.getName());
    }

    public Organisation getManagedOrganisationForClerkId(String clerkId) {
        userService.getByClerkIdOrThrow(clerkId);
        return organisationService.getOrganisationForUser(clerkId).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Organisation not found"));
    }

    public Event getManagedEventForClerkId(Long eventId, String clerkId) {
        Event event = eventService.getEventById(eventId);
        Organisation organisation = getManagedOrganisationForClerkId(clerkId);
        if (event.getOrganisation() == null
                || organisation.getId() == null
                || !organisation.getId().equals(event.getOrganisation().getId())) {
            throw new NoSuchElementException("Event not found with id: " + eventId);
        }
        return event;
    }

    public void deleteEventForClerkId(Long eventId, String clerkId) {
        Event existing = getManagedEventForClerkId(eventId, clerkId);
        eventService.deleteEventById(existing.getId());
    }

    public OrganisationService getOrganisationService() {
        return organisationService;
    }

    public EventService getEventService() {
        return eventService;
    }
}
