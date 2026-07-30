package dev.salt.Ring20.controller;

import dev.salt.Ring20.service.ScheduledCallService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calls")
public class ScheduledCallController {

    private final ScheduledCallService scheduledCallService;

    public ScheduledCallController(ScheduledCallService scheduledCallService) {
        this.scheduledCallService = scheduledCallService;
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("@scheduledCallSecurity.canModify(#id, authentication.name)")
    public ResponseEntity<Void> completeCall(@PathVariable Long id) {

        scheduledCallService.completeCall(id);
        return ResponseEntity.ok().build();
    }
}
