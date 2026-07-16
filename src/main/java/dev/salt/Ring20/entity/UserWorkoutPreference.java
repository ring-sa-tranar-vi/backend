package dev.salt.Ring20.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    private Long userId;

    private Long workoutId;

    @Enumerated(EnumType.STRING)
    private UserWorkoutPreferenceType preferenceType;

    private LocalDateTime createdAt;
}
