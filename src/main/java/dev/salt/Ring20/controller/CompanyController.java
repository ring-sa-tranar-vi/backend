package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.CompanyEventDto;
import dev.salt.Ring20.dto.CompanyMeDto;
import dev.salt.Ring20.dto.CompanyOrganisationDto;
import dev.salt.Ring20.dto.CreateCompanyEventDto;
import dev.salt.Ring20.dto.UpdateCompanyEventDto;
import dev.salt.Ring20.dto.UpdateCompanyOrganisationDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.EventType;
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
    public ResponseEntity<CompanyMeDto> getCompanyMe(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        return ResponseEntity.ok(companyService.getCompanyMe(clerkId));
    }

    @GetMapping("/organisation")
    public ResponseEntity<CompanyOrganisationDto> getOrganisation(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        return ResponseEntity.ok(toOrganisationDto(org));
    }

    @PutMapping("/organisation")
    public ResponseEntity<CompanyOrganisationDto> updateOrganisation(
            Authentication authentication,
            @Valid @RequestBody UpdateCompanyOrganisationDto request) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        var updated =
                companyService
                        .getOrganisationService()
                        .updateOrganisationById(
                                org.getId(),
                                request.name(),
                                request.description(),
                                null,
                                request.orgCity());
        return ResponseEntity.ok(toOrganisationDto(updated));
    }

    @GetMapping("/events")
    public ResponseEntity<List<CompanyEventDto>> getEvents(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        var events =
                companyService.getEventService().getAllEventsByOrgId(org.getId()).stream()
                        .map(this::toEventDto)
                        .toList();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events")
    public ResponseEntity<CompanyEventDto> createEvent(
            Authentication authentication, @Valid @RequestBody CreateCompanyEventDto request) {
        String clerkId = getClerkId(authentication);
        var org = companyService.getManagedOrganisationForClerkId(clerkId);
        if (!org.getId().equals(request.organisation().id())) {
            throw new ResponseStatusException(
                    BAD_REQUEST, "Organisation does not match the authenticated company");
        }
        var created =
                companyService
                        .getEventService()
                        .createEvent(
                                request.name(),
                                request.description(),
                                request.time(),
                                org,
                                request.city(),
                                request.venue(),
                                parseEventType(request.eventType()));
        var response = toEventDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{eventId}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<CompanyEventDto> updateEvent(
            Authentication authentication,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateCompanyEventDto request) {
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
                                existing.getOrganisation(),
                                request.city(),
                                request.venue(),
                                parseEventType(request.eventType()));
        var response = toEventDto(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            Authentication authentication, @PathVariable Long eventId) {
        String clerkId = getClerkId(authentication);
        companyService.deleteEventForClerkId(eventId, clerkId);
        return ResponseEntity.noContent().build();
    }

    private CompanyOrganisationDto toOrganisationDto(
            dev.salt.Ring20.entity.Organisation organisation) {
        return new CompanyOrganisationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }

    private CompanyEventDto toEventDto(Event e) {
        return new CompanyEventDto(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getTime(),
                e.getCity(),
                e.getVenue(),
                e.getUsersAttending(),
                e.getEventType() == null ? null : e.getEventType().name());
    }

    private EventType parseEventType(String eventType) {
        try {
            return EventType.valueOf(eventType);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    BAD_REQUEST, "Unsupported eventType: " + eventType, exception);
        }
    }
}
