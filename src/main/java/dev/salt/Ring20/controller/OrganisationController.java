package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.EventRequestDto;
import dev.salt.Ring20.dto.EventResponseDto;
import dev.salt.Ring20.dto.OrganisationRequestDto;
import dev.salt.Ring20.dto.OrganisationResponseDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
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
@RequestMapping("/api/organisations")
@Tag(
        name = "Organisations",
        description = "Endpoints for creating, managing, and retrieving organisations.")
public class OrganisationController {
    private final OrganisationService service;
    private final EventService eventService;

    public OrganisationController(OrganisationService service, EventService eventService) {
        this.service = service;
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Create organisation", description = "Creates a new organisation.")
    public ResponseEntity<OrganisationResponseDto> createOrganisation(
            @Valid @RequestBody OrganisationRequestDto request) {
        Organisation newOrg =
                service.createOrganisation(
                        request.name(),
                        request.description(),
                        request.orgCity(),
                        request.organizerId());
        OrganisationResponseDto response = toResponseDto(newOrg);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Get all organisations",
            description = "Retrieves all available organisations.")
    public ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok(
                service.getAllOrganisations().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get organisation by ID",
            description = "Retrieves an organisation using its ID.")
    public ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDto(service.getOrganisationById(id)));
    }

    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get organisation events",
            description = "Retrieves all events associated with an organisation.")
    public List<EventResponseDto> getEventsByOrganisation(@PathVariable Long id) {
        return eventService.getAllEventsByOrgId(id).stream().map(this::toEventResponseDto).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(
            summary = "Update organisation",
            description = "Updates an existing organisation by its ID.")
    public ResponseEntity<OrganisationResponseDto> updateOrganisation(
            @PathVariable Long id, @Valid @RequestBody OrganisationRequestDto request) {
        Organisation updatedOrg =
                service.updateOrganisationById(
                        id,
                        request.name(),
                        request.description(),
                        toEvents(request.events()),
                        request.orgCity());
        return ResponseEntity.ok(toResponseDto(updatedOrg));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Delete organisation", description = "Deletes an organisation by its ID.")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        service.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my organisation",
            description = "Retrieves the organisation belonging to the authenticated organiser.")
    public ResponseEntity<OrganisationResponseDto> getMyOrganisation(
            Authentication authentication) {
        return ResponseEntity.ok(
                toResponseDto(service.getOrganisationForUser(authentication.getName())));
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
                events,
                organisation.getOrgCity());
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
