package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.event.EventResponseDto;
import dev.salt.Ring20.dto.organization.OrganizationCreateRequestDto;
import dev.salt.Ring20.dto.organization.OrganizationResponseDto;
import dev.salt.Ring20.dto.organization.OrganizationUpdateRequestDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/organisations")
@Tag(
        name = "Organisations",
        description = "Endpoints for creating, managing, and retrieving organisations.")
public class OrganisationController {

    private final OrganizationService organizationService;
    private final EventService eventService;

    public OrganisationController(OrganizationService service, EventService eventService) {
        this.organizationService = service;
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Create organisation", description = "Creates a new organisation.")
    public ResponseEntity<OrganizationResponseDto> createOrganisation(
            @Valid @RequestBody OrganizationCreateRequestDto request) {
        Organization newOrg =
                organizationService.createOrganisation(
                        OrganizationMapper.toOrganization(request), request.organizerId());
        OrganizationResponseDto response = OrganizationMapper.toResponseDto(newOrg);
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
    public ResponseEntity<List<OrganizationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok()
                .body(
                        organizationService.getAllOrganisations().stream()
                                .map(OrganizationMapper::toResponseDto)
                                .toList());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get organisation by ID",
            description = "Retrieves an organisation using its ID.")
    public ResponseEntity<OrganizationResponseDto> getOrganisationById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(
                        OrganizationMapper.toResponseDto(
                                organizationService.getOrganisationById(id)));
    }

    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get organisation events",
            description = "Retrieves all events associated with an organisation.")
    public ResponseEntity<List<EventResponseDto>> getEventsByOrganisation(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(
                        eventService.getAllEventsByOrgId(id).stream()
                                .map(EventMapper::toEventResponseDto)
                                .toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(
            summary = "Update organisation",
            description = "Updates an existing organisation by its ID.")
    public ResponseEntity<OrganizationResponseDto> updateOrganisation(
            @PathVariable Long id, @Valid @RequestBody OrganizationUpdateRequestDto request) {
        Organization updatedOrg =
                organizationService.updateOrganisationById(
                        OrganizationMapper.toOrganization(request), id);
        return ResponseEntity.ok().body(OrganizationMapper.toResponseDto(updatedOrg));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@organisationSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Delete organisation", description = "Deletes an organisation by its ID.")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organizationService.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my organisation",
            description = "Retrieves the organisation belonging to the authenticated organiser.")
    public ResponseEntity<List<OrganizationResponseDto>> getMyOrganisation(
            Authentication authentication) {
        return ResponseEntity.ok()
                .body(
                        organizationService
                                .getOrganisationForUser(authentication.getName())
                                .stream()
                                .map(OrganizationMapper::toResponseDto)
                                .toList());
    }
}
