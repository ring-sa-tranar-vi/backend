package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Feedback;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);

    List<Feedback> findByWorkoutId(Long workoutId);

    List<Feedback> findByUserIdAndWorkoutId(Long userId, Long workoutId);
}
