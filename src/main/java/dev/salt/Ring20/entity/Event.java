package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;
}
