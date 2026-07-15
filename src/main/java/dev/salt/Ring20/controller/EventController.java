package dev.salt.Ring20.controller;

import dev.salt.Ring20.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@CrossOrigin("http://localhost:5173")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<Void> createEvent() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
