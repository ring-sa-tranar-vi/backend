package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "dashboard_name")
    private String dashboardName;

    @Column(name = "dashboard_description", columnDefinition = "TEXT")
    private String dashboardDescription;

    @Column(name = "subtitle_text", columnDefinition = "TEXT")
    private String subtitleText;

    @Column(name = "instructions_subtitle_text", columnDefinition = "TEXT")
    private String instructionsSubtitleText;

    private Integer level;
    private String type;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    private String instructionsAudio;
    private String workoutAudio;
    private String instructionsImage;
    private String workoutImage;
    private String instructionsVideo;
    private Integer instructionsVideoStart;
    private Integer instructionsVideoStop;
    private Boolean kneeFriendly;
    private Boolean lowImpact;
    private Boolean seated;
    private Boolean beginnerFriendly;

    @Column(nullable = false)
    private Boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
}
