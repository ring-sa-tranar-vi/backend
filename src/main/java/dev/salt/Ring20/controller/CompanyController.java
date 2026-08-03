package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.companyDtos.CompanyMeResponseDto;
import dev.salt.Ring20.dto.eventDtos.EventCreateRequestDto;
import dev.salt.Ring20.dto.eventDtos.EventResponseDto;
import dev.salt.Ring20.dto.eventDtos.EventUpdateRequestDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationResponseDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationUpdateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.service.CompanyService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/company")
@PreAuthorize("hasRole('ORGANIZER')")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    private String getClerkId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt.getSubject();
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyMeResponseDto> getCompanyMe(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        return ResponseEntity.ok(companyService.getCompanyMe(clerkId));
    }

    @GetMapping("/organisation")
    public ResponseEntity<OrganisationResponseDto> getOrganisation(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        var events =
                org.getEvents() == null
                        ? List.<EventResponseDto>of()
                        : org.getEvents().stream().map(this::toEventResponseDto).toList();
        var response =
                new OrganisationResponseDto(
                        org.getId(),
                        org.getName(),
                        org.getDescription(),
                        events,
                        org.getOrgCity(),
                        org.getOrganizer() != null ? org.getOrganizer().getId() : null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/organisation")
    public ResponseEntity<OrganisationResponseDto> updateOrganisation(
            Authentication authentication,
            @Valid @RequestBody OrganisationUpdateRequestDto request) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        var updated =
                companyService
                        .getOrganisationService()
                        .updateOrganisationById(
                                org.getId(),
                                request.name(),
                                request.description(),
                                request.orgCity());
        var events =
                updated.getEvents() == null
                        ? List.<EventResponseDto>of()
                        : updated.getEvents().stream().map(this::toEventResponseDto).toList();
        var response =
                new OrganisationResponseDto(
                        updated.getId(),
                        updated.getName(),
                        updated.getDescription(),
                        events,
                        updated.getOrgCity(),
                        updated.getOrganizer() != null ? updated.getOrganizer().getId() : null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDto>> getEvents(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        var events =
                companyService.getEventService().getAllEventsByOrgId(org.getId()).stream()
                        .map(this::toEventResponseDto)
                        .toList();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponseDto> createEvent(
            Authentication authentication, @Valid @RequestBody EventCreateRequestDto request) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        var created =
                companyService
                        .getEventService()
                        .createEvent(
                                request.name(),
                                request.description(),
                                request.time(),
                                org.getId(),
                                request.city(),
                                request.venue(),
                                request.eventType());
        var response = toEventResponseDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{eventId}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            Authentication authentication,
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateRequestDto request) {
        String clerkId = getClerkId(authentication);
        var existing = companyService.getManagedEventForClerkId(eventId, clerkId);
        var updated =
                companyService
                        .getEventService()
                        .updateEvent(
                                existing.getId(),
                                request.name(),
                                request.description(),
                                request.time(),
                                request.city(),
                                request.venue(),
                                existing.getEventType() == null
                                        ? companyService.getDefaultEventType()
                                        : existing.getEventType());
        var response = toEventResponseDto(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            Authentication authentication, @PathVariable Long eventId) {
        String clerkId = getClerkId(authentication);
        companyService.deleteEventForClerkId(eventId, clerkId);
        return ResponseEntity.noContent().build();
    }

    private EventResponseDto toEventResponseDto(Event e) {
        return new EventResponseDto(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getTime(),
                e.getOrganisation() == null ? null : e.getOrganisation().getId(),
                e.getCity(),
                e.getVenue(),
                e.getEventType());
    }
}
