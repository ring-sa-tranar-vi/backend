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
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Company",
        description = "Endpoints for company users to manage their organisation and events")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "Get current company")
    @GetMapping("/me")
    public ResponseEntity<CompanyMeDto> getCompanyMe(Authentication authentication) {
        return ResponseEntity.ok(companyService.getCompanyMe(getClerkId(authentication)));
    }

    @Operation(summary = "Get managed organisation")
    @GetMapping("/organisation")
    public ResponseEntity<CompanyOrganisationDto> getOrganisation(Authentication authentication) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        return ResponseEntity.ok(toOrganisationDto(organisation));
    }

    @Operation(summary = "Update organisation")
    @PutMapping("/organisation")
    public ResponseEntity<CompanyOrganisationDto> updateOrganisation(
            Authentication authentication,
            @Valid @RequestBody UpdateCompanyOrganisationDto request) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        Organisation updated =
                companyService
                        .getOrganisationService()
                        .updateOrganisationById(
                                organisation.getId(),
                                request.name(),
                                request.description(),
                                request.orgCity());
        return ResponseEntity.ok(toOrganisationDto(updated));
    }

    @Operation(summary = "List organisation events")
    @GetMapping("/events")
    public ResponseEntity<List<CompanyEventDto>> getEvents(Authentication authentication) {
        Organisation organisation =
                companyService.getManagedOrganisationForClerkId(getClerkId(authentication));
        return ResponseEntity.ok(
                companyService.getEventService().getAllEventsByOrgId(organisation.getId()).stream()
                        .map(this::toEventDto)
                        .toList());
    }

    @Operation(summary = "Create event")
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
                        .createEvent(
                                request.name(),
                                request.description(),
                                request.time(),
                                organisation.getId(),
                                request.city(),
                                request.venue(),
                                parseEventType(request.eventType()));
        CompanyEventDto response = toEventDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{eventId}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update event")
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
                        .updateEvent(
                                existing.getId(),
                                request.name(),
                                request.description(),
                                request.time(),
                                request.city(),
                                request.venue(),
                                parseEventType(request.eventType()));
        return ResponseEntity.ok(toEventDto(updated));
    }

    @Operation(summary = "Delete event")
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

    private CompanyOrganisationDto toOrganisationDto(Organisation organisation) {
        return new CompanyOrganisationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }

    private CompanyEventDto toEventDto(Event event) {
        return new CompanyEventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getCity(),
                event.getVenue(),
                event.getUsersAttending(),
                event.getEventType() == null ? null : event.getEventType().name());
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
