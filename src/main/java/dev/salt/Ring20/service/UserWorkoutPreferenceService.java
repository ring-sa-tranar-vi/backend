package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.UserWorkoutPreference;
import dev.salt.Ring20.entity.enums.UserWorkoutPreferenceType;
import dev.salt.Ring20.repository.UserWorkoutPreferenceRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWorkoutPreferenceService {

    private final UserWorkoutPreferenceRepository preferenceRepository;
    private final WorkoutRepository workoutRepository;

    public UserWorkoutPreferenceService(
            UserWorkoutPreferenceRepository preferenceRepository,
            WorkoutRepository workoutRepository) {
        this.preferenceRepository = preferenceRepository;
        this.workoutRepository = workoutRepository;
    }

    public Map<String, List<Long>> getPreferences(Long userId) {
        List<Long> dislikedWorkoutIds = getDislikedWorkoutIds(userId);

        List<Long> favoriteWorkoutIds = getFavouriteWorkoutIds(userId);

        return Map.of(
                "dislikedWorkoutIds", dislikedWorkoutIds, "favoriteWorkoutIds", favoriteWorkoutIds);
    }

    @Transactional
    public UserWorkoutPreference addPreference(
            Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType) {
        return preferenceRepository
                .findByUserIdAndWorkoutIdAndPreferenceType(userId, workoutId, preferenceType)
                .orElseGet(
                        () -> getWorkoutPreference(userId, workoutId, preferenceType));
    }

    private UserWorkoutPreference getWorkoutPreference(Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType) {
        UserWorkoutPreference preference = new UserWorkoutPreference();
        preference.setUserId(userId);
        if (!workoutRepository.existsById(workoutId)) {
            throw new IllegalArgumentException(
                    "Workout does not exist with id: " + workoutId);
        }
        preference.setWorkoutId(workoutId);
        preference.setPreferenceType(preferenceType);
        preference.setCreatedAt(LocalDateTime.now());

        return preferenceRepository.save(preference);
    }

    @Transactional
    public void removePreference(
            Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType) {
        preferenceRepository.deleteByUserIdAndWorkoutIdAndPreferenceType(
                userId, workoutId, preferenceType);
    }

    private List<Long> getDislikedWorkoutIds(Long userId){
        return preferenceRepository
                .findByUserIdAndPreferenceType(userId, UserWorkoutPreferenceType.DISLIKED)
                .stream()
                .map(UserWorkoutPreference::getWorkoutId)
                .toList();
    }
    private List<Long> getFavouriteWorkoutIds(Long userId){
        return  preferenceRepository
                .findByUserIdAndPreferenceType(userId, UserWorkoutPreferenceType.FAVORITE)
                .stream()
                .map(UserWorkoutPreference::getWorkoutId)
                .toList();
    }
}
