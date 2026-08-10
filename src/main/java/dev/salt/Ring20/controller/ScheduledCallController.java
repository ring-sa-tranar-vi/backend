package dev.salt.Ring20.controller;

import dev.salt.Ring20.service.ScheduledCallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calls")
@Tag(
        name = "Calls",
        description = "Endpoints for managing scheduled calls and call completion status.")
public class ScheduledCallController {

    private final ScheduledCallService scheduledCallService;

    public ScheduledCallController(ScheduledCallService scheduledCallService) {
        this.scheduledCallService = scheduledCallService;
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    @Operation(
            summary = "Mark call as complete",
            description = "Marks a scheduled call as completed after the call has been attended.")
    public ResponseEntity<Void> completeCall(@PathVariable Long id) {

        scheduledCallService.completeCall(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    public ResponseEntity<Void> cancelCall(@PathVariable Long id) {

        scheduledCallService.cancelCall(id);
        return ResponseEntity.noContent().build();
    }
}
