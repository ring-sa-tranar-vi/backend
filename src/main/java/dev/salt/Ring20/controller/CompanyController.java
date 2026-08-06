package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.UpdateCompanyEventDto;
import dev.salt.Ring20.dto.UpdateCompanyOrganisationDto;
import dev.salt.Ring20.dto.company.CompanyEventDto;
import dev.salt.Ring20.dto.company.CompanyMeResponseDto;
import dev.salt.Ring20.dto.company.CompanyOrganisationDto;
import dev.salt.Ring20.dto.company.CreateCompanyEventDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.enums.EventType;
import dev.salt.Ring20.mapper.EventMapper;
import dev.salt.Ring20.mapper.OrganizationMapper;
import dev.salt.Ring20.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/company")
@PreAuthorize("@securityService.isOrganizer(authentication.name)")
@Tag(
        name = "Company",
        description = "Endpoints for company users to manage their organisation and events")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "Get current company",
            description = "Returns information about the authenticated company user.")
    @GetMapping("/me")
    public ResponseEntity<CompanyMeResponseDto> getCompanyMe(Authentication authentication) {
        return ResponseEntity.ok().body(companyService.getCompanyMe(getClerkId(authentication)));
    }

    @Operation(summary = "Get managed organisation",
            description = " Returns the organisation managed by the authenticated company.")
    @GetMapping("/organisation")
    public ResponseEntity<CompanyOrganisationDto> getOrganisation(Authentication authentication) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        return ResponseEntity.ok().body(OrganizationMapper.toCompanyOrganisationDto(organisation));
    }

    @Operation(summary = "Update organisation", description = "Updates the authenticated company's organisation.")
    @PutMapping("/organisation")
    public ResponseEntity<CompanyOrganisationDto> updateOrganisation(
            Authentication authentication,
            @Valid @RequestBody UpdateCompanyOrganisationDto request) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        Organisation updated =
                companyService
                        .getOrganisationService()
                        .updateOrganisationById(OrganizationMapper.toOrganization(request),
                                organisation.getId());
        return ResponseEntity.ok().body(OrganizationMapper.toCompanyOrganisationDto(updated));
    }

    @Operation(summary = "List organisation events", description = "Returns all events belonging to the authenticated company's organisation.")
    @GetMapping("/events")
    public ResponseEntity<List<CompanyEventDto>> getEvents(Authentication authentication) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        return ResponseEntity.ok().body(
                companyService.getEventService().getAllEventsByOrgId(organisation.getId()).stream()
                        .map(EventMapper::toCompanyEventDto)
                        .toList());
    }

    @Operation(summary = "Create event", description = "Updates the authenticated company's organisation.")
    @PostMapping("/events")
    public ResponseEntity<CompanyEventDto> createEvent(
            Authentication authentication, @Valid @RequestBody CreateCompanyEventDto request) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        if (!organisation.getId().equals(request.organisation().id())) {
            throw new ResponseStatusException(
                    BAD_REQUEST, "Organisation does not match the authenticated company");
        }
        Event created =
                companyService
                        .getEventService()
                        .createEvent(EventMapper.toEvent(request), organisation.getId());
        CompanyEventDto response = EventMapper.toCompanyEventDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{eventId}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update event", description = "Returns all events belonging to the authenticated company's organisation.")
    @PutMapping("/events/{eventId}")
    public ResponseEntity<CompanyEventDto> updateEvent(
            Authentication authentication,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateCompanyEventDto request) {
        Event existing =
                companyService.getManagedEventForClerkId(eventId, getClerkId(authentication));
        Event updated =
                companyService
                        .getEventService()
                        .updateEvent(EventMapper.toEvent(request), existing.getId());
        return ResponseEntity.ok().body(EventMapper.toCompanyEventDto(updated));
    }

    @Operation(summary = "Delete event", description = "Deletes an event belonging to the authenticated company's organisation.")
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            Authentication authentication, @PathVariable Long eventId) {
        companyService.deleteEventForClerkId(eventId, getClerkId(authentication));
        return ResponseEntity.noContent().build();
    }

    private String getClerkId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt.getSubject();
    }
}
