package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "trainers")
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String prompt;

    private String voice;

    private String intro;

    private String language;

    private String imageSelect;

    private String imageCall;

    private String imageStart;

    private String ambience;

    @OneToMany(mappedBy = "trainer")
    private List<Workout> workouts;
}
