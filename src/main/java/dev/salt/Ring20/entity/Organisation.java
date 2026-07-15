package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    private List<Event> events;

    public Organisation(String name, String description, List<Event> events) {
        this.name = name;
        this.description = description;
        this.events = events;
    }

    public Organisation() {}
}
