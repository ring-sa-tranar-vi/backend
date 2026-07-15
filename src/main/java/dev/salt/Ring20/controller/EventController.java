package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.EventRequestDto;
import dev.salt.Ring20.dto.EventResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@CrossOrigin("http://localhost:5173")
@Transactional
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
}
