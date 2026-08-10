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
        description = "Administrative endpoints for managing organization and events")
public class AdminOrganizationController {

    private final OrganizationService organizationService;
    private final EventService eventService;

    public AdminOrganizationController(
            OrganizationService organizationService, EventService eventService) {
        this.organizationService = organizationService;
        this.eventService = eventService;
    }

    @GetMapping("/organizations")
    @Operation(
            summary = "Get all organizations ",
            description = "Retrieves all available organizations.")
    public ResponseEntity<List<AdminOrganizationDto>> getOrganizations() {
        return ResponseEntity.ok()
                .body(
                        organizationService.getAllOrganizations().stream()
                                .map(OrganizationMapper::toOrganisationDto)
                                .toList());
    }

    @PostMapping("/organizations")
    @Operation(summary = "Create organization", description = "Creates a new organization.")
    public ResponseEntity<AdminOrganizationDto> createOrganization(
            @Valid @RequestBody OrganizationCreateRequestDto request) {
        Organization created =
                organizationService.createOrganization(
                        OrganizationMapper.toOrganization(request), request.organizerId());
        AdminOrganizationDto response = OrganizationMapper.toOrganisationDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/organizations/{id}")
    @Operation(
            summary = "Get organization by ID",
            description = "Retrieves an organization using its ID.")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organizationService.deleteOrganizationById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events")
    @Operation(summary = "Create event", description = "Creates a new event.")
    public ResponseEntity<AdminOrganizationEventDto> createEvent(
            @Valid @RequestBody AdminCreateEventDto request) {
        Organization organisation =
                organizationService.getOrganizationById(request.organisationId());
        Event created =
                eventService.createEvent(
                        EventMapper.toEvent(request, organisation), organisation.getId());
        AdminOrganizationEventDto response = EventMapper.toEventDto(created);
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
}
