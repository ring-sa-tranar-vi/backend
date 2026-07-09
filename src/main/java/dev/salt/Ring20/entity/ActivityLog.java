package dev.salt.Ring20.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(
        indexes = {
            @Index(name = "idx_activity_log_user_id", columnList = "userId"),
            @Index(name = "idx_activity_log_workout_id", columnList = "workoutId")
        })
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long workoutId;

    private LocalDateTime completedAt;

    private Integer durationSeconds;
    private String feedback;

    private String status;
}
