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
@Table(name ="organisation")
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();

    @Min(0)
    private int usersFollowing;

    private String orgCity;

    @ManyToOne
    @JoinColumn(name = "organizer_id", unique = true)
    private User organizer;

    @Column(length = 2000)
    private String motivation;

    public Organisation(
            String name, String description, String orgCity, User organizer, String motivation) {
        this.name = name;
        this.description = description;
        this.usersFollowing = 0;
        this.orgCity = orgCity;
        this.organizer = organizer;
        this.motivation = motivation;
    }
}
