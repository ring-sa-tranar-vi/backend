package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByTrainerId(Long trainerId);

    List<Workout> findByEnabledTrue();

    List<Workout> findByTrainerIdAndEnabledTrue(Long trainerId);

    boolean existsByIdAndEnabledTrue(Long id);
}

