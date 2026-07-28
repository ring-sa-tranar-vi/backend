package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.OrganizationApplicationRequestDto;
import dev.salt.Ring20.dto.OrganizationApplicationResponseDto;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.PaymentStatus;
import dev.salt.Ring20.service.OrganizationApplicationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization-applications")
public class OrganizationApplicationController {

    private final OrganizationApplicationService applicationService;

    public OrganizationApplicationController(OrganizationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> apply(
            @Valid @RequestBody OrganizationApplicationRequestDto request,
            Authentication authentication) {

        OrganizationApplication app =
                applicationService.createApplication(
                        authentication.getName(),
                        request.organizationName(),
                        request.description(),
                        request.motivation());

        URI location = URI.create("/api/organization-applications/" + app.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<List<OrganizationApplicationResponseDto>> getAll() {
        return ResponseEntity.ok(
                applicationService.getAll().stream().map((m -> toResponse(m))).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<OrganizationApplicationResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok(toResponse(applicationService.getById(id)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<OrganizationApplicationResponseDto> approve(@PathVariable Long id) {

        return ResponseEntity.ok(toResponse(applicationService.approve(id)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<OrganizationApplicationResponseDto> reject(@PathVariable Long id) {

        return ResponseEntity.ok(toResponse(applicationService.reject(id)));
    }

    @PutMapping("/{id}/payment-status")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<OrganizationApplicationResponseDto> updatePaymentStatus(
            @PathVariable Long id, @RequestParam PaymentStatus status) {

        OrganizationApplication app = applicationService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(toResponse(app));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private OrganizationApplicationResponseDto toResponse(OrganizationApplication application) {
        return new OrganizationApplicationResponseDto(
                application.getId(),
                application.getUser().getId(),
                application.getOrganizationName(),
                application.getDescription(),
                application.getMotivation(),
                application.getApplicationStatus(),
                application.getCreatedAt(),
                application.getReviewedAt(),
                application.getPaymentStatus());
    }
}
