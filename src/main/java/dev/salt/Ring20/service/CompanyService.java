package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.EventType;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private static final String COMPANY_ROLE = "ORGANIZER";
    private static final EventType DEFAULT_EVENT_TYPE = EventType.IN_PERSON;

    private final UserRepository userRepository;
    private final OrganisationService organisationService;
    private final EventService eventService;

    public CompanyService(
            UserRepository userRepository,
            OrganisationService organisationService,
            EventService eventService) {
        this.userRepository = userRepository;
        this.organisationService = organisationService;
        this.eventService = eventService;
    }

    public CompanyMeResponseDto getCompanyMe() {
        Organisation organisation = getManagedOrganisation();
        User user = getManagedUser();
        return new CompanyMeResponseDto(
                user == null ? null : user.getId(),
                COMPANY_ROLE,
                true,
                organisation.getId(),
                organisation.getName());
    }

    public CompanyOrganisationResponseDto getOrganisation() {
        return toOrganisationResponse(getManagedOrganisation());
    }

    public CompanyOrganisationResponseDto updateOrganisation(
            CompanyOrganisationRequestDto request) {
        Organisation organisation = getManagedOrganisation();
        Organisation updated =
                organisationService.updateOrganisationById(
                        organisation.getId(),
                        request.name(),
                        request.description(),
                        organisation.getEvents(),
                        request.orgCity());
        return toOrganisationResponse(updated);
    }

    public List<CompanyEventResponseDto> getEvents() {
        Organisation organisation = getManagedOrganisation();
        return eventService.getAllEventsByOrgId(organisation.getId()).stream()
                .sorted(
                        Comparator.comparing(
                                Event::getTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toEventResponse)
                .toList();
    }

    public CompanyEventResponseDto createEvent(CompanyEventRequestDto request) {
        Organisation organisation = getManagedOrganisation();
        Event created =
                eventService.createEvent(
                        request.name(),
                        request.description(),
                        request.time(),
                        organisation,
                        request.city(),
                        request.venue(),
                        DEFAULT_EVENT_TYPE);
        return toEventResponse(created);
    }

    public CompanyEventResponseDto updateEvent(Long eventId, CompanyEventRequestDto request) {
        Event existing = getManagedEvent(eventId);
        Event updated =
                eventService.updateEvent(
                        existing.getId(),
                        request.name(),
                        request.description(),
                        request.time(),
                        existing.getOrganisation(),
                        request.city(),
                        request.venue(),
                        existing.getEventType() == null
                                ? DEFAULT_EVENT_TYPE
                                : existing.getEventType());
        return toEventResponse(updated);
    }

    public void deleteEvent(Long eventId) {
        Event existing = getManagedEvent(eventId);
        eventService.deleteEventById(existing.getId());
    }

    private Organisation getManagedOrganisation() {
        return organisationService.getAllOrganisations().stream()
                .min(
                        Comparator.comparing(
                                Organisation::getId, Comparator.nullsLast(Long::compareTo)))
                .orElseThrow(() -> new NoSuchElementException("Company organisation not found"));
    }

    private User getManagedUser() {
        return userRepository.findAll().stream()
                .min(Comparator.comparing(User::getId, Comparator.nullsLast(Long::compareTo)))
                .orElse(null);
    }

    private Event getManagedEvent(Long eventId) {
        Event event = eventService.getEventById(eventId);
        Organisation organisation = getManagedOrganisation();
        if (event.getOrganisation() == null
                || organisation.getId() == null
                || !organisation.getId().equals(event.getOrganisation().getId())) {
            throw new NoSuchElementException("Event not found with id: " + eventId);
        }
        return event;
    }

    private CompanyOrganisationResponseDto toOrganisationResponse(Organisation organisation) {
        return new CompanyOrganisationResponseDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }

    private CompanyEventResponseDto toEventResponse(Event event) {
        return new CompanyEventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getCity(),
                event.getVenue(),
                event.getUsersAttending());
    }
}
