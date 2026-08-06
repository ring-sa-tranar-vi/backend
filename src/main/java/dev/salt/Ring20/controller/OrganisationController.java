package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.eventDtos.EventResponseDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationCreateRequestDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationResponseDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationUpdateRequestDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/organisations")
@Tag(
        name = "Organisations",
        description = "Endpoints for creating, managing, and retrieving organisations.")
public class OrganisationController {


    private final OrganisationService organisationService;
    private final EventService eventService;

    public OrganisationController(OrganisationService service, EventService eventService) {
        this.organisationService = service;
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Create organisation", description = "Creates a new organisation.")
    public ResponseEntity<OrganisationResponseDto> createOrganisation(
            @Valid @RequestBody OrganisationCreateRequestDto request) {
        Organisation newOrg =
                organisationService.createOrganisation(OrganizationMapper.toOrganization(request),
                        request.organizerId());
        OrganisationResponseDto response = OrganizationMapper.toResponseDto(newOrg);
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
        return ResponseEntity.ok().body(
                organisationService.getAllOrganisations().stream().map(OrganizationMapper::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get organisation by ID",
            description = "Retrieves an organisation using its ID.")
    public ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable Long id) {
        return ResponseEntity.ok().body(OrganizationMapper.toResponseDto(organisationService.getOrganisationById(id)));
    }

    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get organisation events",
            description = "Retrieves all events associated with an organisation.")
    public ResponseEntity<List<EventResponseDto>> getEventsByOrganisation(@PathVariable Long id) {
        return ResponseEntity.ok().body(eventService.getAllEventsByOrgId(id).stream().map(EventMapper::toEventResponseDto).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(
            summary = "Update organisation",
            description = "Updates an existing organisation by its ID.")
    public ResponseEntity<OrganisationResponseDto> updateOrganisation(
            @PathVariable Long id, @Valid @RequestBody OrganisationUpdateRequestDto request) {
        Organisation updatedOrg =
                organisationService.updateOrganisationById(OrganizationMapper.toOrganization(request),
                        id);
        return ResponseEntity.ok().body(OrganizationMapper.toResponseDto(updatedOrg));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Delete organisation", description = "Deletes an organisation by its ID.")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my organisation",
            description = "Retrieves the organisation belonging to the authenticated organiser.")
    public ResponseEntity<List<OrganisationResponseDto>> getMyOrganisation(
            Authentication authentication) {
        return ResponseEntity.ok().body(
                organisationService.getOrganisationForUser(authentication.getName()).stream()
                        .map(OrganizationMapper::toResponseDto)
                        .toList());
    }
}
