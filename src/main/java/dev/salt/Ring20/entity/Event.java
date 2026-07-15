package dev.salt.Ring20.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.Data;

@Entity
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;
    private String description;

    @NotNull
    private LocalDateTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    @JsonIgnore
    private Organisation organisation;
}
