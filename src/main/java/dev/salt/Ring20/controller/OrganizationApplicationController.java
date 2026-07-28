package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.OrganizationApplicationRequestDto;
import dev.salt.Ring20.service.OrganizationApplicationService;
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
    public ResponseEntity<String> apply(@RequestBody OrganizationApplicationRequestDto request,  Authentication authentication) {

        applicationService.createApplication(authentication.getName(), request.organizationName(), request.description(), request.motivation());
        return ResponseEntity.ok("Application submitted");
    }


}
