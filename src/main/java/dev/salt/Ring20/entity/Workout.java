package dev.salt.Ring20.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "guidance")
    private String guidance;

    @Column(name = "description")
    private String description;

    @Column(name = "dashboard_name")
    private String dashboardName;

    @Column(name = "dashboard_description", columnDefinition = "TEXT")
    private String dashboardDescription;

    private Integer level;
    private String type;

    @Column(name = "image")
    private String image;

    @Column(name = "video")
    private String video;

    @Column(nullable = false)
    private Boolean enabled = true;
}
