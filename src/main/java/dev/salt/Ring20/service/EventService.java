package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.EventType;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.EventRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event createEvent(
            String name,
            String description,
            LocalDateTime time,
            Organisation organisation,
            String city,
            String venue,
            EventType eventType) {
        return repo.save(new Event(name, description, time, organisation, city, venue, eventType));
    }

    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    public List<Event> getAllEventsByOrgId(Long id) {
        return repo.findByOrganisationId(id);
    }

    public Event getEventById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Event updateEvent(
            Long id,
            String name,
            String description,
            LocalDateTime time,
            Organisation organisation,
            String city,
            String venue,
            EventType eventType) {
        Event event = repo.findById(id).orElseThrow();
        event.setName(name);
        event.setDescription(description);
        event.setTime(time);
        event.setOrganisation(organisation);
        event.setCity(city);
        event.setVenue(venue);
        event.setEventType(eventType);
        return repo.save(event);
    }

    public void deleteEventById(Long id) {
        repo.deleteById(id);
    }
}
