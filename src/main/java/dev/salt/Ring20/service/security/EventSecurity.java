package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class EventSecurity {

    private final EventRepository eventRepository;
    private final SecurityService securityService;

    public EventSecurity(EventRepository eventRepository, SecurityService securityService) {
        this.eventRepository = eventRepository;
        this.securityService = securityService;
    }


    public boolean canModify(Long id, String clerkId) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));

        return securityService.isOwnerOrAdmin(event.getOrganisation().getOrganizer().getId(), clerkId);
    }

}
