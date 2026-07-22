package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.RecommendWorkoutResponseDto;
import dev.salt.Ring20.dto.TrainerRequestDto;
import dev.salt.Ring20.dto.TrainerResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.service.TrainerService;
import dev.salt.Ring20.service.UserService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final UserService userService;

    public TrainerController(TrainerService trainerService, UserService userService) {
        this.trainerService = trainerService;
        this.userService = userService;
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

    @PostMapping
    public ResponseEntity<TrainerResponseDto> createTrainer(
            @RequestBody TrainerRequestDto request, Authentication authentication) {
        assertAdmin(authentication);
        Trainer trainer = trainerService.createTrainer(request);
        return ResponseEntity.ok(toResponseDto(trainer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @PathVariable Long id,
            @RequestBody TrainerRequestDto request,
            Authentication authentication) {
        assertAdmin(authentication);
        Trainer trainer = trainerService.updateTrainer(id, request);
        return ResponseEntity.ok(toResponseDto(trainer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(
            @PathVariable Long id, Authentication authentication) {
        assertAdmin(authentication);
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    private Jwt getJwtOrThrow(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt;
    }

    private void assertAdmin(Authentication authentication) {
        boolean isAdmin = userService.isAdmin(getJwtOrThrow(authentication).getSubject());
        if (!isAdmin) {
            throw new ResponseStatusException(FORBIDDEN, "Admin access required");
        }
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
                .thenApply(ResponseEntity::ok);
    }
}
