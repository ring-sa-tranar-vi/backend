package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event createEvent() {
        return new Event();
    }

    public Event getAllEvents() {
        return new Event();
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
