package dev.salt.Ring20.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "user_workout_preferences",
        indexes = {
                @Index(name = "idx_user_workout_pref_user_id", columnList = "userId"),
                @Index(name = "idx_user_workout_pref_workout_id", columnList = "workoutId")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_workout_pref",
                        columnNames = {"userId", "workoutId", "preferenceType"})
        })
public class UserWorkoutPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long workoutId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserWorkoutPreferenceType preferenceType;

    private LocalDateTime createdAt;
}
