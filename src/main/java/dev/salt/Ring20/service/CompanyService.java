package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.companyDto.CompanyMeResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.enums.EventType;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
 //TODO: document, what is the difference between this and organisation?
    private static final String COMPANY_ROLE = "ORGANIZER";
    private static final EventType DEFAULT_EVENT_TYPE = EventType.IN_PERSON;

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

    public CompanyMeResponseDto getCompanyMe(String clerkId) {
        User user = userService.getByClerkIdOrThrow(clerkId);
        Organisation organisation = getManagedOrganisationForClerkId(clerkId);
        return new CompanyMeResponseDto(
                user == null ? null : user.getId(),
                COMPANY_ROLE,
                true,
                organisation.getId(),
                organisation.getName());
    }

    public Organisation getManagedOrganisationForClerkId(String clerkId) {
        User user = userService.getByClerkIdOrThrow(clerkId);
        return user.getTrainerId() != null
                ? organisationService.getAllOrganisations().stream()
                        .filter(
                                org ->
                                        org.getEvents().stream()
                                                .anyMatch(
                                                        e ->
                                                                e.getOrganisation() != null
                                                                        && e.getOrganisation()
                                                                                .getId()
                                                                                .equals(
                                                                                        org
                                                                                                .getId())))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Organisation not found"))
                : organisationService.getAllOrganisations().stream()
                        .min(
                                Comparator.comparing(
                                        Organisation::getId, Comparator.nullsLast(Long::compareTo)))
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

    //    public CompanyOrganisationResponseDto getOrganisation() {
    //        return toOrganisationResponse(getManagedOrganisation());
    //    }

    //    public CompanyOrganisationResponseDto updateOrganisation(
    //            CompanyOrganisationRequestDto request) {
    //        Organisation organisation = getManagedOrganisation();
    //        Organisation updated =
    //                organisationService.updateOrganisationById(
    //                        organisation.getId(),
    //                        request.name(),
    //                        request.description(),
    //                        organisation.getEvents(),
    //                        request.orgCity());
    //        return toOrganisationResponse(updated);
    //    }

    //    public List<CompanyEventResponseDto> getEvents() {
    //        Organisation organisation = getManagedOrganisation();
    //        return eventService.getAllEventsByOrgId(organisation.getId()).stream()
    //                .sorted(
    //                        Comparator.comparing(
    //                                Event::getTime,
    // Comparator.nullsLast(Comparator.naturalOrder())))
    //                .map(this::toEventResponse)
    //                .toList();
    //    }

    //    public CompanyEventResponseDto createEvent(CompanyEventRequestDto request) {
    //        Organisation organisation = getManagedOrganisation();
    //        Event created =
    //                eventService.createEvent(
    //                        request.name(),
    //                        request.description(),
    //                        request.time(),
    //                        organisation,
    //                        request.city(),
    //                        request.venue(),
    //                        DEFAULT_EVENT_TYPE);
    //        return toEventResponse(created);
    //    }

    //    public CompanyEventResponseDto updateEvent(Long eventId, CompanyEventRequestDto request) {
    //        Event existing = getManagedEvent(eventId);
    //        Event updated =
    //                eventService.updateEvent(
    //                        existing.getId(),
    //                        request.name(),
    //                        request.description(),
    //                        request.time(),
    //                        existing.getOrganisation(),
    //                        request.city(),
    //                        request.venue(),
    //                        existing.getEventType() == null
    //                                ? DEFAULT_EVENT_TYPE
    //                                : existing.getEventType());
    //        return toEventResponse(updated);
    //    }

    //    public void deleteEvent(Long eventId) {
    //        Event existing = getManagedEvent(eventId);
    //        eventService.deleteEventById(existing.getId());
    //    }

    //    public Organisation getManagedOrganisation() {
    //        return organisationService.getAllOrganisations().stream()
    //                .min(
    //                        Comparator.comparing(
    //                                Organisation::getId, Comparator.nullsLast(Long::compareTo)))
    //                .orElseThrow(() -> new NoSuchElementException("Company organisation not
    // found"));
    //    }

    //    private User getManagedUser() {
    //        return userRepository.findAll().stream()
    //                .min(Comparator.comparing(User::getId, Comparator.nullsLast(Long::compareTo)))
    //                .orElse(null);
    //    }

    //    public Event getManagedEvent(Long eventId) {
    //        Event event = eventService.getEventById(eventId);
    //        Organisation organisation = getManagedOrganisation();
    //        if (event.getOrganisation() == null
    //                || organisation.getId() == null
    //                || !organisation.getId().equals(event.getOrganisation().getId())) {
    //            throw new NoSuchElementException("Event not found with id: " + eventId);
    //        }
    //        return event;
    //    }

    public OrganisationService getOrganisationService() {
        return organisationService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public EventType getDefaultEventType() {
        return DEFAULT_EVENT_TYPE;
    }

    //    private CompanyOrganisationResponseDto toOrganisationResponse(Organisation organisation) {
    //        return new CompanyOrganisationResponseDto(
    //                organisation.getId(),
    //                organisation.getName(),
    //                organisation.getDescription(),
    //                organisation.getOrgCity());
    //    }

    //    private CompanyEventResponseDto toEventResponse(Event event) {
    //        return new CompanyEventResponseDto(
    //                event.getId(),
    //                event.getName(),
    //                event.getDescription(),
    //                event.getTime(),
    //                event.getCity(),
    //                event.getVenue(),
    //                event.getUsersAttending());
    //    }
}
