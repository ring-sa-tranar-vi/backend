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
@RequestMapping("/api/organizations")
@Tag(
        name = "organizations",
        description = "Endpoints for creating, managing, and retrieving organizations.")
public class organizationController {

    private final OrganizationService organizationService;
    private final EventService eventService;

    public organizationController(OrganizationService service, EventService eventService) {
        this.organizationService = service;
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Create organization", description = "Creates a new organization.")
    public ResponseEntity<OrganizationResponseDto> createOrganization(
            @Valid @RequestBody OrganizationCreateRequestDto request) {
        Organization newOrg =
                organizationService.createOrganization(
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
            summary = "Get all organizations",
            description = "Retrieves all available organizations.")
    public ResponseEntity<List<OrganizationResponseDto>> getAllOrganizations() {
        return ResponseEntity.ok()
                .body(
                        organizationService.getAllOrganizations().stream()
                                .map(OrganizationMapper::toResponseDto)
                                .toList());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get organization by ID",
            description = "Retrieves an organization using its ID.")
    public ResponseEntity<OrganizationResponseDto> getOrganizationById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(
                        OrganizationMapper.toResponseDto(
                                organizationService.getOrganizationById(id)));
    }

    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get organization events",
            description = "Retrieves all events associated with an organization.")
    public ResponseEntity<List<EventResponseDto>> getEventsByOrganization(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(
                        eventService.getAllEventsByOrgId(id).stream()
                                .map(EventMapper::toEventResponseDto)
                                .toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@organizationSecurity.canModify(#id, authentication.name)")
    @Operation(
            summary = "Update organization",
            description = "Updates an existing organization by its ID.")
    public ResponseEntity<OrganizationResponseDto> updateOrganization(
            @PathVariable Long id, @Valid @RequestBody OrganizationUpdateRequestDto request) {
        Organization updatedOrg =
                organizationService.updateOrganizationById(
                        OrganizationMapper.toOrganization(request), id);
        return ResponseEntity.ok().body(OrganizationMapper.toResponseDto(updatedOrg));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@organizationSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Delete organization", description = "Deletes an organization by its ID.")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganizationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my organization",
            description = "Retrieves the organization belonging to the authenticated organiser.")
    public ResponseEntity<List<OrganizationResponseDto>> getMyorganization(
            Authentication authentication) {
        return ResponseEntity.ok()
                .body(
                        organizationService
                                .getOrganizationForUser(authentication.getName())
                                .stream()
                                .map(OrganizationMapper::toResponseDto)
                                .toList());
    }
}
