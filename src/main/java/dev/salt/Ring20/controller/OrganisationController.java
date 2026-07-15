package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.OrganisationRequestDto;
import dev.salt.Ring20.dto.OrganisationResponseDto;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.service.OrganisationService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/organisations")
@CrossOrigin(
        origins = {"http://localhost:5173", "https://https://prod-ringsatranarvi-app.web.app/"})
public class OrganisationController {
    private OrganisationService service;

    @PostMapping
    private ResponseEntity<Organisation> createOrganisation(
            @RequestBody OrganisationRequestDto request) {
        Organisation newOrg =
                service.createOrganisation(request.name(), request.description(), request.events());
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(newOrg.getId())
                        .toUri();
        return ResponseEntity.created(location).body(newOrg);
    }

    @GetMapping
    private ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        List<Organisation> listOfAllOrgs = service.getAllOrganisations();
        return ResponseEntity.ok(listOfAllOrgs.stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    private ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable Long id) {
        Organisation org = service.getOrganisationById(id);
        return ResponseEntity.ok(
                new OrganisationResponseDto(
                        org.getId(), org.getName(), org.getDescription(), org.getEvents()));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        service.deleteOrganisationById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    private ResponseEntity<Organisation> updateOrganisation(
            @PathVariable Long id, @RequestBody OrganisationRequestDto request) {
        Organisation updatedOrg =
                service.updateOrganisationById(
                        id, request.name(), request.description(), request.events());
        return ResponseEntity.ok(updatedOrg);
    }

    private OrganisationResponseDto toResponseDto(Organisation organisation) {
        return new OrganisationResponseDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getEvents());
    }
}
