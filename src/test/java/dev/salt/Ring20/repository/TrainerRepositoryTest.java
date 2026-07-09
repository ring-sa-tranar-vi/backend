package dev.salt.Ring20.repository;

import static org.junit.jupiter.api.Assertions.*;

import dev.salt.Ring20.entity.Trainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@DisplayName("TrainerRepository Tests")
class TrainerRepositoryTest {

    @Autowired private TrainerRepository trainerRepository;

    @Test
    void existsByNameAndLanguageIsCaseInsensitive() {
        Trainer trainer = new Trainer();
        trainer.setName("Alice Coach");
        trainer.setPrompt("Prompt");
        trainer.setVoice("Voice");
        trainer.setIntro("Intro");
        trainer.setLanguage("en");
        trainerRepository.save(trainer);

        assertTrue(
                trainerRepository.existsByNameIgnoreCaseAndLanguageIgnoreCase("alice coach", "EN"));
    }
}
