package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.CompanyEventRequestDto;
import dev.salt.Ring20.dto.CompanyEventResponseDto;
import dev.salt.Ring20.dto.CompanyMeResponseDto;
import dev.salt.Ring20.dto.CompanyOrganisationRequestDto;
import dev.salt.Ring20.dto.CompanyOrganisationResponseDto;
import dev.salt.Ring20.service.CompanyService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyMeResponseDto> getCompanyMe() {
        return ResponseEntity.ok(companyService.getCompanyMe());
    }

    @GetMapping("/organisation")
    public ResponseEntity<CompanyOrganisationResponseDto> getOrganisation() {
        return ResponseEntity.ok(companyService.getOrganisation());
    }

    @PutMapping("/organisation")
    public ResponseEntity<CompanyOrganisationResponseDto> updateOrganisation(
            @Valid @RequestBody CompanyOrganisationRequestDto request) {
        return ResponseEntity.ok(companyService.updateOrganisation(request));
    }

    @GetMapping("/events")
    public ResponseEntity<List<CompanyEventResponseDto>> getEvents() {
        return ResponseEntity.ok(companyService.getEvents());
    }

    @PostMapping("/events")
    public ResponseEntity<CompanyEventResponseDto> createEvent(
            @Valid @RequestBody CompanyEventRequestDto request) {
        CompanyEventResponseDto response = companyService.createEvent(request);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{eventId}")
                        .buildAndExpand(response.id())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<CompanyEventResponseDto> updateEvent(
            @PathVariable Long eventId, @Valid @RequestBody CompanyEventRequestDto request) {
        return ResponseEntity.ok(companyService.updateEvent(eventId, request));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        companyService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
