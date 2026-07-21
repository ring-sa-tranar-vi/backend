package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.CalendarEventDto;
import dev.salt.Ring20.service.CalendarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar")
@CrossOrigin(origins = {"http://localhost:5173", "https://frontend-training.up.railway.app"})
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    public ResponseEntity<List<CalendarEventDto>> getCalendar(
            @RequestParam Long userId, @RequestParam int year, @RequestParam int month) {

        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }

        List<CalendarEventDto> events = calendarService.getMonthlyCalendar(userId, year, month);

        return ResponseEntity.ok(events);
    }
}
