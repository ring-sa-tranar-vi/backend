package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.UserWorkoutPreference;
import dev.salt.Ring20.entity.UserWorkoutPreferenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserWorkoutPreferenceRepository Tests")
class UserWorkoutPreferenceRepositoryTest {

    @Autowired
    private UserWorkoutPreferenceRepository preferenceRepository;

    @Test
    void saveAndQueryPreferenceWork() {
        UserWorkoutPreference preference = new UserWorkoutPreference();
        preference.setUserId(1L);
        preference.setWorkoutId(2L);
        preference.setPreferenceType(UserWorkoutPreferenceType.DISLIKED);

        preferenceRepository.save(preference);

        assertTrue(preferenceRepository.findByUserIdAndWorkoutIdAndPreferenceType(1L, 2L, UserWorkoutPreferenceType.DISLIKED).isPresent());
    }
}
