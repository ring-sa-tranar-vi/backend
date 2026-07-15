package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
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
    public List<Event> getAllEventsByOrg(Long id) {
        return repo.findByOrganisationId(id);
    }

    public Event getEventById() {
        return new Event();
    }

    public Event deleteEvent() {
        return new Event();
    }

    public Event updateEvent() {
        return new Event();
    }


}
