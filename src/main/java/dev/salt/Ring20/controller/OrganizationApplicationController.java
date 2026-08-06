package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.organisationDtos.OrganizationApplicationRequestDto;
import dev.salt.Ring20.dto.organisationDtos.OrganizationApplicationResponseDto;
import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.enums.PaymentStatus;
import dev.salt.Ring20.mapper.OrganizationApplicationMapper;
import dev.salt.Ring20.service.OrganizationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization-applications")
@Tag(
        name = "Organization Applications",
        description = "Endpoints for managing organisation applications and approval workflows.")
public class OrganizationApplicationController {

    private final OrganizationApplicationService applicationService;

    public OrganizationApplicationController(OrganizationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Create organisation application",
            description = "Creates a new organisation application for admin review.")
    public ResponseEntity<String> apply(
            @Valid @RequestBody OrganizationApplicationRequestDto request,
            Authentication authentication) {

        OrganizationApplication app =
                applicationService.createApplication(OrganizationApplicationMapper.toEntity(request),
                        authentication.getName());

        URI location = URI.create("/api/organization-applications/" + app.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get all applications",
            description = "Retrieves all organisation applications.")
    public ResponseEntity<List<OrganizationApplicationResponseDto>> getAll() {
        return ResponseEntity.ok().body(
                applicationService.getAll().stream().map((OrganizationApplicationMapper::toResponse)).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get application by ID",
            description = "Retrieves an organisation application using its ID.")
    public ResponseEntity<OrganizationApplicationResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok().body(OrganizationApplicationMapper.toResponse(applicationService.getById(id)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Approve application",
            description =
                    "Updates an application status to approved. Used by administrators to approve applications.")
    public ResponseEntity<OrganizationApplicationResponseDto> approve(@PathVariable Long id) {

        return ResponseEntity.ok().body(OrganizationApplicationMapper.toResponse(applicationService.approve(id)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Reject application",
            description =
                    "Updates an application status to rejected. Used by administrators to reject applications.")
    public ResponseEntity<OrganizationApplicationResponseDto> reject(@PathVariable Long id) {

        return ResponseEntity.ok().body(OrganizationApplicationMapper.toResponse(applicationService.reject(id)));
    }

    @PutMapping("/{id}/payment-status")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Update application payment status",
            description = "Updates the payment status of an organisation application.")
    public ResponseEntity<OrganizationApplicationResponseDto> updatePaymentStatus(
            @PathVariable Long id, @RequestParam PaymentStatus status) {

        OrganizationApplication app = applicationService.updatePaymentStatus(id, status);
        return ResponseEntity.ok().body(OrganizationApplicationMapper.toResponse(app));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Delete application",
            description = "Deletes an organisation application by its ID.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
