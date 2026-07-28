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

import java.net.URI;
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

        OrganizationApplication app = applicationService.createApplication(
                authentication.getName(),
                request.organizationName(),
                request.description(),
                request.motivation()
        );

        URI location = URI.create("/api/organization-applications/" + app.getId());

        return ResponseEntity.created(location).build();
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

    @PutMapping("/{id}/approve")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<String> approve(@PathVariable Long id) {

        applicationService.approve(id);
        return ResponseEntity.ok("Application approved");
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<String> reject(@PathVariable Long id) {

        applicationService.reject(id);
        return ResponseEntity.ok("Application rejected");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private OrganizationApplicationResponseDto toResponse(OrganizationApplication application) {
        return new OrganizationApplicationResponseDto(application.getId(), application.getUser().getId(), application.getOrganizationName(), application.getDescription(), application.getMotivation(), application.getApplicationStatus(), application.getCreatedAt(), application.getReviewedAt());
    }

}
