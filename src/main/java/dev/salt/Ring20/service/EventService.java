package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event createEvent(String name, String description, LocalDateTime time, Organisation organisation) {
        return repo.save(new Event(name, description, time, organisation));
    }

    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    public List<Event> getAllEventsByOrgId(Long id) {
        return repo.findByOrganisationId(id);
    }

    public Event getEventById(Long id) {
        return repo.findById(id)
                .orElseThrow();
    }

    public Event updateEvent(Long id, String name, String description, LocalDateTime time, Organisation organisation) {
        Event event = repo.findById(id).orElseThrow();
        event.setName(name);
        event.setDescription(description);
        event.setTime(time);
        event.setOrganisation(organisation);
        return repo.save(event);
    }

    public void deleteEventById(Long id) {
        repo.deleteById(id);
    }
}
