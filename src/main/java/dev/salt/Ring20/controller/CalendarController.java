package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.CalendarEventDto;
import dev.salt.Ring20.service.CalendarService;
import dev.salt.Ring20.service.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar")
@CrossOrigin(origins = {"http://localhost:5173", "https://frontend-training.up.railway.app"})
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Endpoints for retrieving user workout calendar events.")
public class CalendarController {

    private final CalendarService calendarService;
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("@securityService.isOwnerOrAdmin(#userId, authentication.name)")
    @Operation(
            summary = "Get monthly calendar events",
            description =
                    "Retrieves a user's workout calendar events for the specified month and year.")
    public ResponseEntity<List<CalendarEventDto>> getCalendar(
            @RequestParam Long userId, @RequestParam int year, @RequestParam int month) {

        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }

        List<CalendarEventDto> events = calendarService.getMonthlyCalendar(userId, year, month);

        return ResponseEntity.ok(events);
    }
}
