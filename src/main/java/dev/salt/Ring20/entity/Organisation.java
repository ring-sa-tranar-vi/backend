package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    @Min(0)
    private int usersFollowing;

    private String orgCity;

    public Organisation(String name, String description, List<Event> events, String orgCity) {
        this.name = name;
        this.description = description;
        this.events = events;
        this.usersFollowing = 0;
        this.orgCity = orgCity;
    }
}
