package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Workout;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByEnabledTrue();

    List<Workout> findByTrainerIdAndEnabledTrue(Long trainerId);

    List<Workout> findAllByIdIn(List<Long> ids);

    boolean existsByIdAndEnabledTrue(Long id);
}
