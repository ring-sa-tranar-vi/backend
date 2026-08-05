package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.eventDtos.EventCreateRequestDto;
import dev.salt.Ring20.dto.eventDtos.EventResponseDto;
import dev.salt.Ring20.dto.eventDtos.EventUpdateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.mapper.EventMapper;
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

    // TODO: use the same way of sending ResponseEntity, either .ok(whats in the body) or
    // .ok().body(whats in the body) not both

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("@organisationSecurity.canModify(#request.organisationId, authentication.name)")
    @Operation(summary = "Create event", description = "Creates a new event.")
    public ResponseEntity<EventResponseDto> createEvent(
            @Valid @RequestBody EventCreateRequestDto request) {
        Event event =
                eventService.createEvent(EventMapper.toEvent(request), request.organisationId());

        EventResponseDto response = EventMapper.toEventResponseDto(event);
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
        return ResponseEntity.ok().body(eventService.getAllEvents().stream().map(EventMapper::toEventResponseDto).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves an event using its ID.")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok().body(EventMapper.toEventResponseDto(eventService.getEventById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@eventSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Delete event", description = "Deletes an event by its ID.")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("@eventSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Update event", description = "Updates an existing event by its ID.")
    public ResponseEntity<EventResponseDto> updateEventById(
            @PathVariable Long id, @Valid @RequestBody EventUpdateRequestDto request) {
        Event updatedEvent =
                eventService.updateEvent(EventMapper.toEvent(request), id);
        return ResponseEntity.ok().body(EventMapper.toEventResponseDto(updatedEvent));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my events",
            description = "Retrieves all events created by the authenticated organiser.")
    public ResponseEntity<List<EventResponseDto>> getMyEvents(Authentication auth) {
        return  ResponseEntity.ok().body(eventService.getEventsForUser(auth.getName()).stream().map(EventMapper::toEventResponseDto).toList());
    }
}
