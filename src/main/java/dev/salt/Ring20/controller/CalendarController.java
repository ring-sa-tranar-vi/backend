package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.calendarEvent.CalendarEventDto;
import dev.salt.Ring20.mapper.CalendarMapper;
import dev.salt.Ring20.service.CalendarService;
import dev.salt.Ring20.service.data.CalendarEventData;
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

    @GetMapping
    @PreAuthorize("@securityService.isOwnerOrAdmin(#userId, authentication.name)")
    @Operation(
            summary = "Get monthly calendar events",
            description =
                    "Retrieves a user's workout calendar events for the specified month and year.")
    public ResponseEntity<List<CalendarEventDto>> getCalendar(
            @RequestParam Long userId, @RequestParam int year, @RequestParam int month) {
        int minMonth = 1;
        int maxMonth = 12;
        if (month < minMonth || month > maxMonth) {
            return ResponseEntity.badRequest().build();
        }

        List<CalendarEventData> events = calendarService.getMonthlyCalendar(userId, year, month);

        return ResponseEntity.ok().body(events.stream().map(CalendarMapper::toDto).toList());
    }
}
