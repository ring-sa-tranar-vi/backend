package dev.salt.Ring20.controller;

import dev.salt.Ring20.entity.ScheduledCall;
import dev.salt.Ring20.service.ScheduledCallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    @Operation(
            summary = "Get scheduled call",
            description = "Returns a scheduled call by its ID.")
    public ResponseEntity<ScheduledCall> getCall(@PathVariable Long id) {

        return ResponseEntity.ok(
                scheduledCallService.getCall(id)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Get user's scheduled calls",
            description = "Returns all scheduled calls for a user.")
    public ResponseEntity<List<ScheduledCall>> getCallsForUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                scheduledCallService.getCallsForUser(userId)
        );
    }

    @PostMapping("/user/{userId}/reset")
    @Operation(
            summary = "Reset user's future calls",
            description = "Cancels future pending calls and recreates weekly calls.")
    public ResponseEntity<Void> resetCallsForUser(
            @PathVariable Long userId) {

        scheduledCallService.resetAllCallsForUser(userId);
        return ResponseEntity.noContent().build();
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
