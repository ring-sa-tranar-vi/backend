package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.workoutDtos.WorkoutEnabledRequestDto;
import dev.salt.Ring20.dto.workoutDtos.WorkoutRequestDto;
import dev.salt.Ring20.dto.workoutDtos.WorkoutResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.service.FileStorageService;
import dev.salt.Ring20.service.WorkoutService;
import dev.salt.Ring20.service.security.CurrentUserService;
import dev.salt.Ring20.service.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
@Tag(
        name = "Workouts",
        description = "Endpoints for managing workouts and tracking user workout activities.")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final FileStorageService fileStorageService;
    private final CurrentUserService currentUserService;
    private final SecurityService securityService;

    public WorkoutController(
            WorkoutService workoutService,
            FileStorageService fileStorageService,
            CurrentUserService currentUserService,
            SecurityService securityService) {
        this.workoutService = workoutService;
        this.fileStorageService = fileStorageService;
        this.currentUserService = currentUserService;
        this.securityService = securityService;
    }

    @GetMapping
    @Operation(summary = "Get all workouts", description = "Retrieves all available workouts.")
    public ResponseEntity<List<WorkoutResponseDto>> getAllWorkouts(Authentication authentication) {
        boolean includeDisabled = securityService.isAdminIfAuthenticated(authentication);
        List<Workout> workouts = workoutService.getAllWorkouts(includeDisabled);
        return ResponseEntity.ok().body(workouts.stream().map(this::toWorkoutResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workout by ID", description = "Retrieves a workout using its ID.")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(
            @PathVariable Long id, Authentication authentication) {
        boolean includeDisabled = securityService.isAdminIfAuthenticated(authentication);
        Workout workout = workoutService.getWorkoutById(id, includeDisabled);

        return ResponseEntity.ok(toWorkoutResponse(workout));
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Create workout",
            description = "Creates a new workout. Available to administrators only.")
    public ResponseEntity<WorkoutResponseDto> createWorkout(
            @Valid @RequestBody WorkoutRequestDto workoutRequest) {
        Workout createdWorkout = workoutService.createWorkout(toEntity(workoutRequest));
        return ResponseEntity.ok(toWorkoutResponse(createdWorkout));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Update workout",
            description = "Updates an existing workout. Available to administrators only.")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(
            @PathVariable Long id, @Valid @RequestBody WorkoutRequestDto workoutRequest) {
        Workout updatedWorkout = workoutService.updateWorkout(id, toEntity(workoutRequest));
        return ResponseEntity.ok(toWorkoutResponse(updatedWorkout));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Delete workout",
            description = "Deletes a workout by its ID. Available to administrators only.")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enabled")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Set workout enabled status",
            description = "Enables or disables a workout. Available to administrators only.")
    public ResponseEntity<WorkoutResponseDto> setWorkoutEnabled(
            @PathVariable Long id, @Valid @RequestBody WorkoutEnabledRequestDto request) {
        if (request.enabled() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                toWorkoutResponse(workoutService.setWorkoutEnabled(id, request.enabled())));
    }

    @PostMapping("/{id}/start")
    @Operation(
            summary = "Start workout",
            description = "Starts a workout session for the authenticated user.")
    public ResponseEntity<WorkoutResponseDto> startWorkout(
            @PathVariable Long id, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        Workout workout = workoutService.startWorkout(id, userId);
        return ResponseEntity.ok().body(toWorkoutResponse(workout));
    }

    private WorkoutResponseDto toWorkoutResponse(Workout workout) {
        String imageUrl =
                (workout.getImage() != null)
                        ? fileStorageService.getFileAccess(workout.getImage(), 15)
                        : null;
        String videoUrl =
                (workout.getVideo() != null)
                        ? fileStorageService.getFileAccess(workout.getVideo(), 15)
                        : null;

        return new WorkoutResponseDto(
                workout.getId(),
                workout.getName(),
                workout.getDescription(),
                workout.getDashboardName(),
                workout.getDashboardDescription(),
                workout.getInstructions(),
                workout.getGuidance(),
                workout.getLevel(),
                workout.getType(),
                imageUrl,
                videoUrl,
                workout.getEnabled());
    }

    private Workout toEntity(WorkoutRequestDto request) {
        Workout workout = new Workout();
        workout.setName(request.name());
        workout.setDescription(request.description());
        workout.setDashboardName(request.dashboardName());
        workout.setDashboardDescription(request.dashboardDescription());
        workout.setInstructions(request.instructions());
        workout.setGuidance(request.guidance());
        workout.setLevel(request.level());
        workout.setType(request.type());
        workout.setImage(request.image());
        workout.setVideo(request.video());

        return workout;
    }
}
