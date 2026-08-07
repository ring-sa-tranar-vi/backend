package dev.salt.Ring20.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import dev.salt.Ring20.service.data.NormalizedTrainerData;
import dev.salt.Ring20.service.data.RecommendedWorkoutData;
import dev.salt.Ring20.service.data.TrainerData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final GeminiWorkoutService geminiWorkoutService;
    private final WorkoutRepository workoutRepository;
    private final ObjectMapper objectMapper; // Needed to process json chunks locally

    public TrainerService(
            TrainerRepository trainerRepository,
            UserRepository userRepository,
            GeminiWorkoutService geminiWorkoutService,
            WorkoutRepository workoutRepository,
            ObjectMapper objectMapper) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.geminiWorkoutService = geminiWorkoutService;
        this.workoutRepository = workoutRepository;
        this.objectMapper = objectMapper;
    }

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    @Transactional
    public Trainer createTrainer(TrainerData request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        NormalizedTrainerData data = normalizedTrainerData(request);

        validateUniqueNameAndLanguage(data);

        Trainer trainer = new Trainer();
        return getTrainer(request, data.name(), data.prompt(), data.voice(), data.intro(), data.language(), trainer);
    }

    @Transactional
    public Trainer updateTrainer(Long id, TrainerData request) {
        validateId(id);

        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        Trainer trainer =
                trainerRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Trainer not found with id: " + id));

        NormalizedTrainerData data = normalizedTrainerData(request);

        validateNameAndLanguageForUpdate(data, trainer);

        return getTrainer(request, data.name(), data.prompt(), data.voice(), data.intro(), data.language(), trainer);
    }

    public Trainer getTrainerById(Long id) {
        validateId(id);
        return trainerRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trainer not found with id: " + id));
    }

    @Transactional
    public void deleteTrainer(Long id) {
        validateId(id);
        Trainer trainer = getTrainerById(id);
        trainerRepository.delete(trainer);
    }

    public CompletableFuture<RecommendedWorkoutData> getAiRecommendedWorkout(Long userId) {
        validateId(userId);
        List<Workout> workouts = getEnabledWorkouts();
        User user = getUser(userId);

        return geminiWorkoutService
                .recommendWorkoutWithReasoning(user, workouts)
                .thenApply(this::parseRecommendedWorkout);
    }

    private void validateUniqueNameAndLanguage(NormalizedTrainerData data) {
        boolean exist = trainerRepository.existsByNameIgnoreCaseAndLanguageIgnoreCase(data.name(), data.language());
        if (exist) {
            throw new IllegalArgumentException(
                    "Trainer with name '"
                            + data.name()
                            + "' already exists in language '"
                            + data.language()
                            + "'");
        }
    }

    private void validateNameAndLanguageForUpdate(NormalizedTrainerData data, Trainer trainer) {
        boolean exist = trainerRepository.existsByNameIgnoreCaseAndLanguageIgnoreCase(data.name(), data.language());
        if (exist
                && (!data.name().equalsIgnoreCase(trainer.getName())
                || !data.language().equalsIgnoreCase(trainer.getLanguage()))) {
            throw new IllegalArgumentException(
                    "Trainer with name '"
                            + data.name()
                            + "' already exists in language '"
                            + data.language()
                            + "'");
        }
    }

    private NormalizedTrainerData normalizedTrainerData(TrainerData request) {
        String name = normalizeRequired(request.name(), "name", 120);
        String prompt = normalizeRequired(request.prompt(), "prompt", 8000);
        String voice = normalizeRequired(request.voice(), "voice", 120);
        String intro = normalizeRequired(request.intro(), "intro", 2048);
        String language = normalizeRequired(request.language(), "language", 40);

        return new NormalizedTrainerData(name, prompt, voice, intro, language);
    }

    private Trainer getTrainer(TrainerData request, String name, String prompt, String voice, String intro, String language, Trainer trainer) {

        trainer.setName(name);
        trainer.setPrompt(prompt);
        trainer.setVoice(voice);
        trainer.setIntro(intro);
        trainer.setLanguage(language);
        trainer.setImageSelect(normalizeOptional(request.imageSelect(), "imageSelect", 2048));
        trainer.setImageCall(normalizeOptional(request.imageCall(), "imageCall", 2048));
        trainer.setImageStart(normalizeOptional(request.imageStart(), "imageStart", 2048));
        trainer.setAmbience(normalizeOptional(request.ambience(), "ambience", 255));

        return trainerRepository.save(trainer);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be a positive number");
        }
    }

    private String normalizeRequired(String value, String fieldName, int maxLength) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " exceeds max length " + maxLength);
        }

        return normalized;
    }

    private String normalizeOptional(String value, String fieldName, int maxLength) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " exceeds max length " + maxLength);
        }

        return normalized;
    }

    private List<Workout> getEnabledWorkouts() {
        List<Workout> workouts = workoutRepository.findByEnabledTrue();

        if (workouts.isEmpty()) {
            throw new NoSuchElementException("No workouts found");
        }

        return workouts;
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    }

    private RecommendedWorkoutData parseRecommendedWorkout(String jsonResponse) {
        try {
            JsonNode node = objectMapper.readTree(jsonResponse);

            Long workoutId = extractWorkoutId(node);
            validateRecommendedWorkout(workoutId);

            String nodePath = "reasoning";

            String reasoning = node.path(nodePath).asText("No reasoning provided.");

            return new RecommendedWorkoutData(workoutId, reasoning);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse structured AI recommendation payload", e);
        }
    }

    private Long extractWorkoutId(JsonNode node) {
        String nodePath = "workoutId";
        JsonNode idNode = node.path(nodePath);

        if (idNode.isMissingNode() || idNode.isNull()) {
            return null;
        }

        return idNode.asLong();
    }

    private void validateRecommendedWorkout(Long workoutId) {
        if (workoutId != null && !workoutRepository.existsByIdAndEnabledTrue(workoutId)) {

            throw new IllegalStateException("AI recommended invalid workout id: " + workoutId);
        }
    }
}
