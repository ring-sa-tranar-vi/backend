package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.WorkoutEnabledRequestDto;
import dev.salt.Ring20.dto.WorkoutRequestDto;
import dev.salt.Ring20.dto.WorkoutResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.service.GeminiWorkoutService;
import dev.salt.Ring20.service.UserService;
import dev.salt.Ring20.service.WorkoutService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final UserService userService;

    public WorkoutController(
            WorkoutService workoutService,
            UserService userService,
            GeminiWorkoutService geminiWorkoutService) {
        this.workoutService = workoutService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponseDto>> getAllWorkouts(Authentication authentication) {
        boolean includeDisabled = isAdminIfAuthenticated(authentication);
        List<Workout> workouts =workoutService.getAllWorkouts(includeDisabled);
        return ResponseEntity.ok().body(
                workouts.stream()
                        .map(this::toWorkoutResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(
            @PathVariable Long id, Authentication authentication) {
        boolean includeDisabled = isAdminIfAuthenticated(authentication);
        Workout workout =
                workoutService.getWorkoutById(id, includeDisabled);

        return ResponseEntity.ok(toWorkoutResponse(workout));
    }

    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(
            @RequestBody WorkoutRequestDto workoutRequest, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }
        Workout createdWorkout = workoutService.createWorkout(toEntity(workoutRequest));
        return ResponseEntity.ok(toWorkoutResponse(createdWorkout));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(
            @PathVariable Long id,
            @RequestBody WorkoutRequestDto workoutRequest,
            Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }
        Workout updatedWorkout = workoutService.updateWorkout(id, toEntity(workoutRequest));
        return ResponseEntity.ok(toWorkoutResponse(updatedWorkout));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enabled")
    public ResponseEntity<WorkoutResponseDto> setWorkoutEnabled(
            @PathVariable Long id,
            @RequestBody WorkoutEnabledRequestDto request,
            Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }
        if (request.enabled() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                toWorkoutResponse(
                        workoutService.setWorkoutEnabled(id, request.enabled())
                ));
    }

    @GetMapping("/{id}/audio")
    public ResponseEntity<String> getWorkoutAudio(@PathVariable Long id) {
        return ResponseEntity.ok().body(workoutService.getWorkoutAudioUrl(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<WorkoutResponseDto> startWorkout(
            @PathVariable Long id, @RequestParam(required = false) Long userId) {
        Workout workout = workoutService.startWorkout(id, userId);
        return ResponseEntity.ok().body(toWorkoutResponse(workout));
    }

    private WorkoutResponseDto toWorkoutResponse(Workout workout) {
        WorkoutResponseDto.TrainerIdDTO trainerDTO = null;

        if (workout.getTrainer() != null) {
            trainerDTO = new WorkoutResponseDto.TrainerIdDTO(workout.getTrainer().getId());
        }

        return new WorkoutResponseDto(
                workout.getId(),
                workout.getName(),
                workout.getDescription(),
                workout.getDashboardName(),
                workout.getDashboardDescription(),
                workout.getSubtitleText(),
                workout.getInstructionsSubtitleText(),
                workout.getLevel(),
                workout.getType(),
                workout.getDurationSeconds(),
                workout.getInstructionsAudio(),
                workout.getWorkoutAudio(),
                workout.getInstructionsImage(),
                workout.getWorkoutImage(),
                workout.getInstructionsVideo(),
                workout.getInstructionsVideoStart(),
                workout.getInstructionsVideoStop(),
                workout.getKneeFriendly(),
                workout.getLowImpact(),
                workout.getSeated(),
                workout.getBeginnerFriendly(),
                workout.getEnabled(),
                trainerDTO);
    }

    private Workout toEntity(WorkoutRequestDto request) {
        Workout workout = new Workout();
        workout.setName(request.name());
        workout.setDescription(request.description());
        workout.setDashboardName(request.dashboardName());
        workout.setDashboardDescription(request.dashboardDescription());
        workout.setSubtitleText(request.subtitleText());
        workout.setInstructionsSubtitleText(request.instructionsSubtitleText());
        workout.setLevel(request.level());
        workout.setType(request.type());
        workout.setDurationSeconds(request.durationSeconds());
        workout.setInstructionsAudio(request.instructionsAudio());
        workout.setWorkoutAudio(request.workoutAudio());
        workout.setInstructionsImage(request.instructionsImage());
        workout.setWorkoutImage(request.workoutImage());
        workout.setInstructionsVideo(request.instructionsVideo());
        workout.setInstructionsVideoStart(request.instructionsVideoStart());
        workout.setInstructionsVideoStop(request.instructionsVideoStop());
        workout.setKneeFriendly(request.kneeFriendly());
        workout.setLowImpact(request.lowImpact());
        workout.setSeated(request.seated());
        workout.setBeginnerFriendly(request.beginnerFriendly());

        if (request.trainer() != null && request.trainer().id() != null) {
            Trainer trainer = new Trainer();
            trainer.setId(request.trainer().id());
            workout.setTrainer(trainer);
        }

        return workout;
    }

    private Jwt getJwtOrThrow(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt;
    }

    private boolean isAdmin(Authentication authentication) {
        return userService.isAdmin(getJwtOrThrow(authentication).getSubject());
    }

    private boolean isAdminIfAuthenticated(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }
        return userService.isAdmin(jwt.getSubject());
    }
}
