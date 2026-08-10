package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.admin.AdminCreateEventDto;
import dev.salt.Ring20.dto.admin.AdminOrganisationDto;
import dev.salt.Ring20.dto.admin.AdminOrganisationEventDto;
import dev.salt.Ring20.dto.organization.OrganisationCreateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.mapper.EventMapper;
import dev.salt.Ring20.mapper.OrganizationMapper;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("@securityService.isAdmin(authentication.name)")
@Tag(
        name = "Admin Organizations",
        description = "Administrative endpoints for managing organisation and events")
public class AdminOrganisationController {

    private final OrganisationService organisationService;
    private final EventService eventService;

    public AdminOrganisationController(
            OrganisationService organisationService, EventService eventService) {
        this.organisationService = organisationService;
        this.eventService = eventService;
    }

    @GetMapping("/organisations")
    @Operation(
            summary = "Get all organisations ",
            description = "Retrieves all available organisations.")
    public ResponseEntity<List<AdminOrganisationDto>> getOrganisations() {
        return ResponseEntity.ok()
                .body(
                        organisationService.getAllOrganisations().stream()
                                .map(this::toOrganisationDto)
                                .toList());
    }

    @PostMapping("/organisations")
    @Operation(summary = "Create organisation", description = "Creates a new organisation.")
    public ResponseEntity<AdminOrganisationDto> createOrganisation(
            @Valid @RequestBody OrganisationCreateRequestDto request) {
        Organisation created =
                organisationService.createOrganisation(
                        OrganizationMapper.toOrganization(request), request.organizerId());
        AdminOrganisationDto response = toOrganisationDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/organisations/{id}")
    @Operation(
            summary = "Get organisation by ID",
            description = "Retrieves an organisation using its ID.")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events")
    @Operation(summary = "Create event", description = "Creates a new event.")
    public ResponseEntity<AdminOrganisationEventDto> createEvent(
            @Valid @RequestBody AdminCreateEventDto request) {
        Organisation organisation =
                organisationService.getOrganisationById(request.organisationId());
        Event created =
                eventService.createEvent(
                        EventMapper.toEvent(request, organisation), organisation.getId());
        AdminOrganisationEventDto response = toEventDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "Delete event", description = "Deletes an event by its ID.")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }

    private AdminOrganisationDto toOrganisationDto(Organisation organisation) {
        List<AdminOrganisationEventDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream().map(this::toEventDto).toList();
        return new AdminOrganisationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events,
                organisation.getOrgCity(),
                organisation.getOrganizer() == null ? null : organisation.getOrganizer().getId());
    }

    private AdminOrganisationEventDto toEventDto(Event event) {
        return new AdminOrganisationEventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getOrganisation() == null ? null : event.getOrganisation().getId());
    }
}
