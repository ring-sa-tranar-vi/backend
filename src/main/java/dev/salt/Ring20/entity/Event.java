package dev.salt.Ring20.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    private String description;

    @NotNull
    private LocalDateTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    @JsonIgnore
    private Organisation organisation;

    public Event(String name, String description, LocalDateTime time, Organisation organisation) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.organisation = organisation;
    }

}
