package dev.salt.Ring20.integration;

import static org.junit.jupiter.api.Assertions.*;

import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.service.ScheduledCallService;
import dev.salt.Ring20.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@Import(UserService.class)
@DisplayName("UserService Integration Tests")
class UserServiceIntegrationTest {

    @Autowired private UserService userService;

    @Autowired private UserRepository userRepository;

    @Autowired private TrainerRepository trainerRepository;
    @MockitoBean private ScheduledCallService scheduledCallService;

    @Test
    void createUserPersistsAndCanBeLoaded() {
        User created = userService.createUser("clerk_int_1", "Integration User");

        assertNotNull(created.getId());

        User saved = userRepository.findByClerkId("clerk_int_1").orElseThrow();

        assertEquals("Integration User", saved.getName());
    }

    @Test
    void updateUserPreferencesPersistsChanges() {
        Trainer trainer = new Trainer();
        trainer.setName("Test Trainer");
        trainer.setLanguage("English");
        trainer.setPrompt("Test prompt");
        trainer.setVoice("Test voice");
        trainer.setIntro("Test intro");

        trainer = trainerRepository.save(trainer);

        userService.createUser("clerk_int_2", "Original");
        User preferences = new User("Updated", 5, "context", "clerk_int_2");
        preferences.setTrainerId(trainer.getId());
        preferences.setCity("Stockholm");
        preferences.setOnboarding(false);

        User updated = userService.updateUserPreferencesByClerkId("clerk_int_2",  preferences.getName(), preferences.getIntensityLevel(), preferences.getContext(),preferences.getTrainerId(), preferences.getCity(), preferences.isOnboarding());

        assertEquals("Updated", updated.getName());
        assertEquals(5, updated.getIntensityLevel());
        assertEquals(trainer.getId(), updated.getTrainerId());
        assertEquals("Stockholm", updated.getCity());

        User saved = userRepository.findByClerkId("clerk_int_2").orElseThrow();

        assertEquals("Updated", saved.getName());
    }
}
