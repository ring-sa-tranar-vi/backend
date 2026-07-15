package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.EventRequestDto;
import dev.salt.Ring20.dto.EventResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin("http://localhost:5173")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto request) {
        Event event = service.createEvent(request.name(), request.description(), request.time(), request.organisation());
        EventResponseDto response = toResponse(event);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(event.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    private EventResponseDto toResponse(Event event) {
        return new EventResponseDto(event.getId(), event.getName(), event.getDescription(), event.getTime(), event.getOrganisation().getId());
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        validatePositiveId(id);
        return ResponseEntity.ok(toResponse(service.getEventById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id) {
        validatePositiveId(id);
        service.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEventById(@PathVariable Long id, @RequestBody EventRequestDto request) {
        validatePositiveId(id);
        Event updatedEvent = service.updateEvent(id, request.name(), request.description(), request.time(), request.organisation());
        return ResponseEntity.ok(toResponse(updatedEvent));
    }

    private void validatePositiveId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id must be a positive number");
        }
    }

}
