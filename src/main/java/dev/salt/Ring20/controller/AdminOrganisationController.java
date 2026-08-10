package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.admin.AdminCreateEventDto;
import dev.salt.Ring20.dto.admin.AdminOrganizationDto;
import dev.salt.Ring20.dto.admin.AdminOrganizationEventDto;
import dev.salt.Ring20.dto.organization.OrganizationCreateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.mapper.EventMapper;
import dev.salt.Ring20.mapper.OrganizationMapper;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganizationService;
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

    private final OrganizationService organizationService;
    private final EventService eventService;

    public AdminOrganisationController(
            OrganizationService organizationService, EventService eventService) {
        this.organizationService = organizationService;
        this.eventService = eventService;
    }

    @GetMapping("/organisations")
    @Operation(
            summary = "Get all organisations ",
            description = "Retrieves all available organisations.")
    public ResponseEntity<List<AdminOrganizationDto>> getOrganisations() {
        return ResponseEntity.ok()
                .body(
                        organizationService.getAllOrganisations().stream()
                                .map(this::toOrganisationDto)
                                .toList());
    }

    @PostMapping("/organisations")
    @Operation(summary = "Create organisation", description = "Creates a new organisation.")
    public ResponseEntity<AdminOrganizationDto> createOrganisation(
            @Valid @RequestBody OrganizationCreateRequestDto request) {
        Organization created =
                organizationService.createOrganisation(
                        OrganizationMapper.toOrganization(request), request.organizerId());
        AdminOrganizationDto response = toOrganisationDto(created);
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
        organizationService.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events")
    @Operation(summary = "Create event", description = "Creates a new event.")
    public ResponseEntity<AdminOrganizationEventDto> createEvent(
            @Valid @RequestBody AdminCreateEventDto request) {
        Organization organisation =
                organizationService.getOrganisationById(request.organisationId());
        Event created =
                eventService.createEvent(
                        EventMapper.toEvent(request, organisation), organisation.getId());
        AdminOrganizationEventDto response = toEventDto(created);
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

    private AdminOrganizationDto toOrganisationDto(Organization organisation) {
        List<AdminOrganizationEventDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream().map(this::toEventDto).toList();
        return new AdminOrganizationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events,
                organisation.getOrgCity(),
                organisation.getOrganizer() == null ? null : organisation.getOrganizer().getId());
    }

    private AdminOrganizationEventDto toEventDto(Event event) {
        return new AdminOrganizationEventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getOrganisation() == null ? null : event.getOrganisation().getId());
    }
}
