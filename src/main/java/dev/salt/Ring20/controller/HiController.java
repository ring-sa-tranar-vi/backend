package dev.salt.Ring20.controller;

import dev.salt.Ring20.entity.GreetingMessage;
import dev.salt.Ring20.service.HiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HiController {

  private final HiService service;

  public HiController(HiService hiservice) {
    this.service = hiservice;
  }

  @GetMapping("/hi")
  public ResponseEntity<GreetingMessage> sayHi() {
    return ResponseEntity.ok(service.getOrCreateMessage());
  }
}
