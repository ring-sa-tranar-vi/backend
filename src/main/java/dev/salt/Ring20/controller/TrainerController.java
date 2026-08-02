package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.RecommendWorkoutResponseDto;
import dev.salt.Ring20.dto.TrainerRequestDto;
import dev.salt.Ring20.dto.TrainerResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.service.FileStorageService;
import dev.salt.Ring20.service.TrainerService;
import dev.salt.Ring20.service.data.RecommendedWorkoutData;
import dev.salt.Ring20.service.data.TrainerData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@Tag(
        name = "Trainers",
        description = "Endpoints for managing trainers and generating workout recommendations.")
public class TrainerController {

    private final TrainerService trainerService;
    private final FileStorageService fileStorageService;

    public TrainerController(TrainerService trainerService, FileStorageService fileStorageService) {
        this.trainerService = trainerService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    @Operation(summary = "Get all trainers", description = "Retrieves all available trainers.")
    public ResponseEntity<List<TrainerResponseDto>> getAllTrainers() {
        return ResponseEntity.ok(
                trainerService.getAllTrainers().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trainer by ID", description = "Retrieves a trainer using their ID.")
    public ResponseEntity<TrainerResponseDto> getTrainerById(@PathVariable Long id) {
        Trainer trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(toResponseDto(trainer));
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Create trainer",
            description = "Creates a new trainer. Available to administrators only.")
    public ResponseEntity<TrainerResponseDto> createTrainer(
            @Valid @RequestBody TrainerRequestDto request) {
        Trainer trainer = trainerService.createTrainer(toTrainerData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(trainer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Update trainer",
            description = "Updates an existing trainer. Available to administrators only.")
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @PathVariable Long id, @Valid @RequestBody TrainerRequestDto request) {
        Trainer trainer = trainerService.updateTrainer(id, toTrainerData(request));
        return ResponseEntity.ok(toResponseDto(trainer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Delete trainer",
            description = "Deletes a trainer by ID. Available to administrators only.")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    private TrainerResponseDto toResponseDto(Trainer trainer) {
        String introUrl =
                (trainer.getIntro() != null)
                        ? fileStorageService.getFileAccess(trainer.getIntro(), 15)
                        : null;
        String imageSelectUrl =
                (trainer.getImageSelect() != null)
                        ? fileStorageService.getFileAccess(trainer.getImageSelect(), 15)
                        : null;
        String imageCallUrl =
                (trainer.getImageCall()) != null
                        ? fileStorageService.getFileAccess(trainer.getImageCall(), 15)
                        : null;
        String imageStartUrl =
                (trainer.getImageStart()) != null
                        ? fileStorageService.getFileAccess(trainer.getImageStart(), 15)
                        : null;
        return new TrainerResponseDto(
                trainer.getId(),
                trainer.getName(),
                trainer.getPrompt(),
                trainer.getVoice(),
                introUrl,
                trainer.getLanguage(),
                imageSelectUrl,
                imageCallUrl,
                imageStartUrl,
                trainer.getAmbience());
    }

    @GetMapping("/{trainerId}/recommend-for/{userId}")
    @Operation(
            summary = "Delete trainer",
            description = "Deletes a trainer by ID. Available to administrators only.")
    public CompletableFuture<ResponseEntity<RecommendWorkoutResponseDto>>
            getTrainerAiRecommendation(@PathVariable Long trainerId, @PathVariable Long userId) {

        return trainerService
                .getAiRecommendedWorkout(trainerId, userId)
                .thenApply(data -> ResponseEntity.ok(toRecommendedWorkoutResponse(data)));
    }

    private RecommendWorkoutResponseDto toRecommendedWorkoutResponse(RecommendedWorkoutData data) {
        return new RecommendWorkoutResponseDto(data.workoutId(), data.reasoning());
    }

    private TrainerData toTrainerData(TrainerRequestDto request) {
        return new TrainerData(
                request.name(),
                request.prompt(),
                request.voice(),
                request.intro(),
                request.language(),
                request.imageSelect(),
                request.imageCall(),
                request.imageStart(),
                request.ambience());
    }
}
