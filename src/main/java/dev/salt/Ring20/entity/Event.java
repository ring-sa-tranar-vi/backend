package dev.salt.Ring20.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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

    @NotBlank private String name;
    private String description;

    @NotNull private LocalDateTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    @JsonIgnore
    private Organisation organisation;

    @NotNull private String city;
    private String venue;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EventType eventType;

    @NotNull
    @Min(0)
    private int usersAttending;

    public Event(
            String name,
            String description,
            LocalDateTime time,
            Organisation organisation,
            String city,
            String venue,
            EventType eventType) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.organisation = organisation;
        this.city = city;
        this.venue = venue;
        this.eventType = eventType;
        this.usersAttending = 0;
    }

    public Event() {}
}
