package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.ScheduledCallDto;
import dev.salt.Ring20.entity.ScheduledCall;
import dev.salt.Ring20.mapper.ScheduledCallMapper;
import dev.salt.Ring20.service.ScheduledCallService;
import dev.salt.Ring20.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

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
    private final UserService userService;

    public ScheduledCallController(
            ScheduledCallService scheduledCallService, UserService userService) {
        this.scheduledCallService = scheduledCallService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    @Operation(summary = "Get scheduled call", description = "Returns a scheduled call by its ID.")
    public ResponseEntity<ScheduledCallDto> getCall(@PathVariable Long id) {

        return ResponseEntity.ok(ScheduledCallMapper.toScheduledCallDto(scheduledCallService.getCall(id)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Get user's scheduled calls",
            description = "Returns all scheduled calls for a user.")
    public ResponseEntity<List<ScheduledCallDto>> getCallsForUser(@PathVariable Long userId) {

        return ResponseEntity.ok(
                scheduledCallService.getCallsForUser(userId)
                        .stream()
                        .map(ScheduledCallMapper::toScheduledCallDto)
                        .toList());
    }

    @PostMapping("/user/{userId}/reset")
    @Operation(
            summary = "Reset user's future calls",
            description = "Cancels future pending calls and recreates weekly calls.")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    public ResponseEntity<Void> resetCallsForUser(@PathVariable Long userId) {

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

    @PostMapping("/{id}/received")
    @Operation(
            summary = "Confirm callback notification received",
            description = "Confirms that the native app received the callback notification.")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    public ResponseEntity<Void> confirmReceived(@PathVariable Long id) {
        scheduledCallService.confirmReceived(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    public ResponseEntity<Void> cancelCall(@PathVariable Long id) {

        scheduledCallService.cancelCall(id);
        return ResponseEntity.noContent().build();
    }
}
