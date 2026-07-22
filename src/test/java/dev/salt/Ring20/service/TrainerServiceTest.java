package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.service.data.TrainerData;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService Tests")
class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;

    @InjectMocks private TrainerService trainerService;

    private Trainer trainer;
    private TrainerData trainerRequest;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Alice Coach");
        trainer.setPrompt("Prompt");
        trainer.setVoice("Voice");
        trainer.setIntro("Intro");
        trainer.setLanguage("en");

        trainerRequest =
                new TrainerData(
                        "Alice Coach", "Prompt", "Voice", "Intro", "en", null, null, null, null);
    }

    @Test
    void createTrainerSavesValidRequest() {
        when(trainerRepository.existsByNameIgnoreCaseAndLanguageIgnoreCase("Alice Coach", "en"))
                .thenReturn(false);
        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer created = trainerService.createTrainer(trainerRequest);

        assertEquals("Alice Coach", created.getName());
    }

    @Test
    void createTrainerRejectsNullBody() {
        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class, () -> trainerService.createTrainer(null));
        assertEquals("Request body is required", ex.getMessage());
    }

    @Test
    void createTrainerRejectsDuplicateNameAndLanguage() {
        when(trainerRepository.existsByNameIgnoreCaseAndLanguageIgnoreCase("Alice Coach", "en"))
                .thenReturn(true);

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> trainerService.createTrainer(trainerRequest));
        assertEquals("Trainer already exists for this language", ex.getMessage());
    }

    @Test
    void getTrainerByIdThrowsWhenMissing() {
        when(trainerRepository.findById(9L)).thenReturn(Optional.empty());

        NoSuchElementException ex =
                assertThrows(
                        NoSuchElementException.class, () -> trainerService.getTrainerById(9L));
        assertEquals("Trainer not found with id: 9", ex.getMessage());
    }

    @Test
    void deleteTrainerDeletesExistingTrainer() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        trainerService.deleteTrainer(1L);

        verify(trainerRepository).delete(trainer);
    }
}
