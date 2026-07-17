package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.EventRequestDto;
import dev.salt.Ring20.dto.EventResponseDto;
import dev.salt.Ring20.dto.OrganisationRequestDto;
import dev.salt.Ring20.dto.OrganisationResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/organisations")
@CrossOrigin(
        origins = {"http://localhost:5173", "https://https://prod-ringsatranarvi-app.web.app/"})
public class OrganisationController {
    private final OrganisationService service;
    private final EventService eventService;

    public OrganisationController(OrganisationService service, EventService eventService) {
        this.service = service;
        this.eventService = eventService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<OrganisationResponseDto> createOrganisation(
            @Valid @RequestBody OrganisationRequestDto request) {
        Organisation newOrg =
                service.createOrganisation(
                        request.name(), request.description(), toEvents(request.events()));
        OrganisationResponseDto response = toResponseDto(newOrg);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok(
                service.getAllOrganisations().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}/events")
    public List<EventResponseDto> getEventsByOrganisation(@PathVariable Long id) {
        validatePositiveId(id);
        return eventService.getAllEventsByOrgId(id).stream().map(this::toEventResponseDto).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable Long id) {
        validatePositiveId(id);
        return ResponseEntity.ok(toResponseDto(service.getOrganisationById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        validatePositiveId(id);
        service.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<OrganisationResponseDto> updateOrganisation(
            @PathVariable Long id, @Valid @RequestBody OrganisationRequestDto request) {
        validatePositiveId(id);
        Organisation updatedOrg =
                service.updateOrganisationById(
                        id, request.name(), request.description(), toEvents(request.events()));
        return ResponseEntity.ok(toResponseDto(updatedOrg));
    }

    private void validatePositiveId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id must be a positive number");
        }
    }

    private List<Event> toEvents(List<EventRequestDto> requests) {
        if (requests == null) {
            return null;
        }

        return requests.stream().map(this::toEvent).toList();
    }

    private Event toEvent(EventRequestDto request) {
        Event event = new Event();
        event.setName(request.name());
        event.setDescription(request.description());
        event.setTime(request.time());
        return event;
    }

    private OrganisationResponseDto toResponseDto(Organisation organisation) {
        List<EventResponseDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream().map(this::toEventResponseDto).toList();
        return new OrganisationResponseDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events);
    }

    private EventResponseDto toEventResponseDto(Event event) {
        Long organisationId =
                event.getOrganisation() == null ? null : event.getOrganisation().getId();
        return new EventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                organisationId,
                event.getCity(),
                event.getVenue(),
                event.getEventType());
    }
}
