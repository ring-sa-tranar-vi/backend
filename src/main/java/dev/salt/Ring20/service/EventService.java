package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.OrganisationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final OrganisationRepository organisationRepository;
    private final OrganisationService organisationService;

    public EventService(
            EventRepository eventRepository,
            OrganisationRepository organisationRepository,
            OrganisationService organisationService) {
        this.eventRepository = eventRepository;
        this.organisationRepository = organisationRepository;
        this.organisationService = organisationService;
    }

    public Event createEvent(Event event, Long organizationId) {
        event.setOrganisation(getOrganisationById(organizationId));
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAllWithOrganisation();
    }

    public List<Event> getAllEventsByOrgId(Long id) {
        return eventRepository.findByOrganisationId(id);
    }

    public Event getEventById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException(" Event not found with id: " + id));
    }

    @Transactional
    public Event updateEvent(Event event, Long id) {

        Event eventToUpdate =
                eventRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                " Event not found with id: " + id));
        eventToUpdate.setName(event.getName());
        eventToUpdate.setDescription(event.getDescription());
        eventToUpdate.setTime(event.getTime());
        eventToUpdate.setCity(event.getCity());
        eventToUpdate.setVenue(event.getVenue());
        eventToUpdate.setEventType(event.getEventType());
        return eventRepository.save(eventToUpdate);
    }

    @Transactional
    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getEventsForUser(String clerkId) {
        List<Organisation> organisations =
                organisationRepository.findByOrganizer_ClerkIdWithEvents(clerkId);

        if (organisations.isEmpty()) {
            throw new NoSuchElementException(
                    "No organisations found for user with clerkId: " + clerkId);
        }

        return organisations.stream().flatMap(org -> org.getEvents().stream()).toList();
    }

    private Organisation getOrganisationById(Long id) {
        return organisationService.getOrganisationById(id);
    }
}
