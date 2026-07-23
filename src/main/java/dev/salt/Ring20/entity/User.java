package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    private static final Long DEFAULT_TRAINER_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer intensityLevel;
    private String context;

    @Column(unique = true, nullable = false)
    private String clerkId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Long trainerId;
    private String city;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CallbackPreference> callbackPreferences = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_organisations")
    private List<Organisation> followedOrganisations = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_events")
    private List<Event> attendingEvents = new ArrayList<>();

    public User(String name, Integer intensityLevel, String context, String clerkId) {
        this.name = name;
        this.intensityLevel = intensityLevel;
        this.context = context;
        this.clerkId = clerkId;
        this.role = UserRole.USER;
        this.trainerId = DEFAULT_TRAINER_ID;
    }
}
