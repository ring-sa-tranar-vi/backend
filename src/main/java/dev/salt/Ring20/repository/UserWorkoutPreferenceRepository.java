package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.UserWorkoutPreference;
import dev.salt.Ring20.entity.UserWorkoutPreferenceType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWorkoutPreferenceRepository extends JpaRepository<UserWorkoutPreference, Long> {
    List<UserWorkoutPreference> findByUserIdAndPreferenceType(Long userId, UserWorkoutPreferenceType preferenceType);

    Optional<UserWorkoutPreference> findByUserIdAndWorkoutIdAndPreferenceType(Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType);

    void deleteByUserIdAndWorkoutIdAndPreferenceType(Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType);
}
