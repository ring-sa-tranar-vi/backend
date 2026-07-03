package dev.salt.Ring20.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HiController {

    private final HiService hiService;

    public HiController(HiService hiservice) {
        this.hiService = hiservice;
    }

    @GetMapping("/hi")
    public ResponseEntity<String> sayHi() {
        return ResponseEntity.ok("");
    }
}
