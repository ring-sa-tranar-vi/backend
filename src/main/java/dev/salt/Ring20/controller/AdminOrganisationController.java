package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.AdminCreateEventDto;
import dev.salt.Ring20.dto.AdminOrganisationDto;
import dev.salt.Ring20.dto.AdminOrganisationEventDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationCreateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.EventType;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("@securityService.isAdmin(authentication.name)")
public class AdminOrganisationController {

    private final OrganisationService organisationService;
    private final EventService eventService;

    public AdminOrganisationController(
            OrganisationService organisationService, EventService eventService) {
        this.organisationService = organisationService;
        this.eventService = eventService;
    }

    @GetMapping("/organisations")
    public ResponseEntity<List<AdminOrganisationDto>> getOrganisations() {
        return ResponseEntity.ok(
                organisationService.getAllOrganisations().stream()
                        .map(this::toOrganisationDto)
                        .toList());
    }

    @PostMapping("/organisations")
    public ResponseEntity<AdminOrganisationDto> createOrganisation(
            @Valid @RequestBody OrganisationCreateRequestDto request) {
        Organisation created =
                organisationService.createOrganisation(
                        request.name(),
                        request.description(),
                        request.orgCity(),
                        request.organizerId(), );
        AdminOrganisationDto response = toOrganisationDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/organisations/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events")
    public ResponseEntity<AdminOrganisationEventDto> createEvent(
            @Valid @RequestBody AdminCreateEventDto request) {
        Organisation organisation =
                organisationService.getOrganisationById(request.organisationId());
        Event created =
                eventService.createEvent(
                        request.name(),
                        request.description(),
                        request.time(),
                        organisation.getId(),
                        organisation.getOrgCity(),
                        null,
                        EventType.IN_PERSON);
        AdminOrganisationEventDto response = toEventDto(created);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/events/{id}")
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
