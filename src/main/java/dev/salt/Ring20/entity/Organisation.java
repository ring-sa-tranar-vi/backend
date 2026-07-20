package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank private String name;
    private String description;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    @NotNull
    @Min(0)
    private int usersFollowing;

    @NotNull @NotBlank private String orgCity;

    public Organisation(String name, String description, List<Event> events, String orgCity) {
        this.name = name;
        this.description = description;
        this.events = events;
        this.usersFollowing = 0;
        this.orgCity = orgCity;
    }
}
