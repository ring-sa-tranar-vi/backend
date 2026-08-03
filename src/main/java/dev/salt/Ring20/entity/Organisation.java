package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
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
    private List<Event> events = new ArrayList<>();

    @Min(0)
    private int usersFollowing;

    private String orgCity;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    public Organisation(String name, String description, String orgCity, User organizer) {
        this.name = name;
        this.description = description;
        this.usersFollowing = 0;
        this.orgCity = orgCity;
        this.organizer = organizer;
    }
}
