package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.EventRequestDto;
import dev.salt.Ring20.dto.EventResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Endpoints for creating, managing, and retrieving events.")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("@organisationSecurity.canModify(#request.organisationId, authentication.name)")
    @Operation(summary = "Create event", description = "Creates a new event.")
    public ResponseEntity<EventResponseDto> createEvent(
            @Valid @RequestBody EventRequestDto request) {
        Event event =
                service.createEvent(
                        request.name(),
                        request.description(),
                        request.time(),
                        request.organisationId(),
                        request.city(),
                        request.venue(),
                        request.eventType());

        EventResponseDto response = toResponse(event);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(event.getId())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieves all available events.")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves an event using its ID.")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(service.getEventById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Delete event", description = "Deletes an event by its ID.")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id) {
        service.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#request.organisationId, authentication.name)")
    @Operation(summary = "Update event", description = "Updates an existing event by its ID.")
    public ResponseEntity<EventResponseDto> updateEventById(
            @PathVariable Long id, @Valid @RequestBody EventRequestDto request) {
        Event updatedEvent =
                service.updateEvent(
                        id,
                        request.name(),
                        request.description(),
                        request.time(),
                        request.organisationId(),
                        request.city(),
                        request.venue(),
                        request.eventType());
        return ResponseEntity.ok(toResponse(updatedEvent));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my events",
            description = "Retrieves all events created by the authenticated organiser.")
    public List<EventResponseDto> getMyEvents(Authentication auth) {
        return service.getEventsForUser(auth.getName()).stream().map(this::toResponse).toList();
    }

    private EventResponseDto toResponse(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getOrganisation().getId(),
                event.getCity(),
                event.getVenue(),
                event.getEventType());
    }
}
