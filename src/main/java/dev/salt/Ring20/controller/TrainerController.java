package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.trainerDtos.TrainerRequestDto;
import dev.salt.Ring20.dto.trainerDtos.TrainerResponseDto;
import dev.salt.Ring20.dto.workoutDtos.RecommendWorkoutResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.mapper.TrainerMapper;
import dev.salt.Ring20.service.TrainerService;
import dev.salt.Ring20.service.storage.FileStorageService;
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

    private static final int VALID_MINUTES = 15;

    private final TrainerService trainerService;
    private final FileStorageService fileStorageService;

    public TrainerController(TrainerService trainerService, FileStorageService fileStorageService) {
        this.trainerService = trainerService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all trainers", description = "Retrieves all available trainers.")
    public ResponseEntity<List<TrainerResponseDto>> getAllTrainers() {
        return ResponseEntity.ok()
                .body(
                        trainerService.getAllTrainers().stream()
                                .map(
                                        t ->
                                                TrainerMapper.toResponseDto(
                                                        t, fileStorageService, VALID_MINUTES))
                                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trainer by ID", description = "Retrieves a trainer using their ID.")
    public ResponseEntity<TrainerResponseDto> getTrainerById(@PathVariable Long id) {
        Trainer trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok()
                .body(TrainerMapper.toResponseDto(trainer, fileStorageService, VALID_MINUTES));
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Create trainer",
            description = "Creates a new trainer. Available to administrators only.")
    public ResponseEntity<TrainerResponseDto> createTrainer(
            @Valid @RequestBody TrainerRequestDto request) {
        Trainer trainer = trainerService.createTrainer(TrainerMapper.toTrainerData(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TrainerMapper.toResponseDto(trainer, fileStorageService, VALID_MINUTES));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Update trainer",
            description = "Updates an existing trainer. Available to administrators only.")
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @PathVariable Long id, @Valid @RequestBody TrainerRequestDto request) {
        Trainer trainer = trainerService.updateTrainer(id, TrainerMapper.toTrainerData(request));
        return ResponseEntity.ok()
                .body(TrainerMapper.toResponseDto(trainer, fileStorageService, VALID_MINUTES));
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

    @GetMapping("/recommend-for/{userId}")
    @Operation(
            summary = "Get AI workout recommendation",
            description = "Generates an AI recommended workout for a user based on a trainer.")
    public CompletableFuture<ResponseEntity<RecommendWorkoutResponseDto>>
            getTrainerAiRecommendation(@PathVariable Long userId) {

        return trainerService
                .getAiRecommendedWorkout(userId)
                .thenApply(
                        data ->
                                ResponseEntity.ok()
                                        .body(TrainerMapper.toRecommendedWorkoutResponse(data)));
    }
}
