package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GreetingMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    public GreetingMessage() {

    }

    public GreetingMessage(String message) {
        this.message = message;
    }
}
