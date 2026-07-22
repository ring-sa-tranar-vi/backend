package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.RecommendWorkoutResponseDto;
import dev.salt.Ring20.dto.TrainerRequestDto;
import dev.salt.Ring20.dto.TrainerResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.service.TrainerService;
import dev.salt.Ring20.service.UserService;
import dev.salt.Ring20.service.data.RecommendedWorkoutData;
import dev.salt.Ring20.service.data.TrainerData;
import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService, UserService userService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<TrainerResponseDto>> getAllTrainers() {
        return ResponseEntity.ok(
                trainerService.getAllTrainers().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponseDto> getTrainerById(@PathVariable Long id) {
        Trainer trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(toResponseDto(trainer));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TrainerResponseDto> createTrainer(
            @Valid @RequestBody TrainerRequestDto request) {
        Trainer trainer = trainerService.createTrainer(toTrainerData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(trainer));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @PathVariable Long id, @Valid @RequestBody TrainerRequestDto request) {
        Trainer trainer = trainerService.updateTrainer(id, toTrainerData(request));
        return ResponseEntity.ok(toResponseDto(trainer));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    private TrainerResponseDto toResponseDto(Trainer trainer) {
        return new TrainerResponseDto(
                trainer.getId(),
                trainer.getName(),
                trainer.getPrompt(),
                trainer.getVoice(),
                trainer.getIntro(),
                trainer.getLanguage(),
                trainer.getImageSelect(),
                trainer.getImageCall(),
                trainer.getImageStart(),
                trainer.getAmbience());
    }

    @GetMapping("/{trainerId}/recommend-for/{userId}")
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
