package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.UserWorkoutPreference;
import dev.salt.Ring20.entity.UserWorkoutPreferenceType;
import dev.salt.Ring20.repository.UserWorkoutPreferenceRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWorkoutPreferenceService {

    private final UserWorkoutPreferenceRepository preferenceRepository;

    public UserWorkoutPreferenceService(UserWorkoutPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public Map<String, List<Long>> getPreferences(Long userId) {
        List<Long> dislikedWorkoutIds =
                preferenceRepository
                        .findByUserIdAndPreferenceType(userId, UserWorkoutPreferenceType.DISLIKED)
                        .stream()
                        .map(UserWorkoutPreference::getWorkoutId)
                        .toList();

        List<Long> favoriteWorkoutIds =
                preferenceRepository
                        .findByUserIdAndPreferenceType(userId, UserWorkoutPreferenceType.FAVORITE)
                        .stream()
                        .map(UserWorkoutPreference::getWorkoutId)
                        .toList();

        return Map.of(
                "dislikedWorkoutIds", dislikedWorkoutIds, "favoriteWorkoutIds", favoriteWorkoutIds);
    }

    @Transactional
    public UserWorkoutPreference addPreference(
            Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType) {
        return preferenceRepository
                .findByUserIdAndWorkoutIdAndPreferenceType(userId, workoutId, preferenceType)
                .orElseGet(
                        () -> {
                            UserWorkoutPreference preference = new UserWorkoutPreference();
                            preference.setUserId(userId);
                            preference.setWorkoutId(workoutId);
                            preference.setPreferenceType(preferenceType);
                            preference.setCreatedAt(LocalDateTime.now());

                            return preferenceRepository.save(preference);
                        });
    }

    @Transactional
    public void removePreference(
            Long userId, Long workoutId, UserWorkoutPreferenceType preferenceType) {
        preferenceRepository.deleteByUserIdAndWorkoutIdAndPreferenceType(
                userId, workoutId, preferenceType);
    }
}
