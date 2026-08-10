package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.company.CompanyMeResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private static final String COMPANY_ROLE = "COMPANY";

    private final OrganizationService organizationService;
    private final EventService eventService;
    private final UserService userService;

    public CompanyService(
            OrganizationService organizationService,
            EventService eventService,
            UserService userService) {
        this.organizationService = organizationService;
        this.eventService = eventService;
        this.userService = userService;
    }

    public CompanyMeResponseDto getCompanyMe(String clerkId) {
        User user = userService.getByClerkIdOrThrow(clerkId);
        Organisation organisation =
                organizationService.findOrganisationForUser(clerkId).orElse(null);
        boolean canManageOrganisation = organisation != null;
        return new CompanyMeResponseDto(
                user.getId(),
                canManageOrganisation
                        ? COMPANY_ROLE
                        : user.getRole() == null ? "USER" : user.getRole().name(),
                canManageOrganisation,
                canManageOrganisation ? organisation.getId() : null,
                canManageOrganisation ? organisation.getName() : null);
    }

    public Organisation getManagedOrganisationForClerkId(String clerkId) {
        userService.getByClerkIdOrThrow(clerkId);
        return organizationService.getOrganisationForUser(clerkId).stream()
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

    public OrganizationService getOrganisationService() {
        return organizationService;
    }

    public EventService getEventService() {
        return eventService;
    }
}
