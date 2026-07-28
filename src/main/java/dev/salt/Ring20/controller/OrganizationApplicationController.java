package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.OrganizationApplicationRequestDto;
import dev.salt.Ring20.dto.OrganizationApplicationResponseDto;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.service.OrganizationApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization-applications")
public class OrganizationApplicationController {

    private final OrganizationApplicationService applicationService;

    public OrganizationApplicationController(OrganizationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> apply(@RequestBody OrganizationApplicationRequestDto request, Authentication authentication) {

        applicationService.createApplication(authentication.getName(), request.organizationName(), request.description(), request.motivation());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Application submitted");
    }

    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<List<OrganizationApplicationResponseDto>> getAll() {
        return ResponseEntity.ok(
                applicationService.getAll().stream()
                        .map((m -> toResponse(m)))
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<OrganizationApplicationResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(toResponse(applicationService.getById(id)));
    }



    private OrganizationApplicationResponseDto toResponse(OrganizationApplication application) {
        return new OrganizationApplicationResponseDto(application.getId(), application.getUser().getId(), application.getOrganizationName(), application.getDescription(), application.getMotivation(), application.getStatus(), application.getCreatedAt(), application.getReviewedAt());
    }

}
