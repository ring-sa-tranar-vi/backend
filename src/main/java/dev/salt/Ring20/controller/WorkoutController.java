package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.workoutDtos.WorkoutEnabledRequestDto;
import dev.salt.Ring20.dto.workoutDtos.WorkoutRequestDto;
import dev.salt.Ring20.dto.workoutDtos.WorkoutResponseDto;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.mapper.WorkoutMapper;
import dev.salt.Ring20.service.WorkoutService;
import dev.salt.Ring20.service.security.CurrentUserService;
import dev.salt.Ring20.service.security.SecurityService;
import dev.salt.Ring20.service.storage.FileStorageService;
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

    private static final int VALID_MINUTES = 15;

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
        return ResponseEntity.ok()
                .body(
                        workouts.stream()
                                .map(
                                        w ->
                                                WorkoutMapper.toWorkoutResponse(
                                                        w, fileStorageService, VALID_MINUTES))
                                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workout by ID", description = "Retrieves a workout using its ID.")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(
            @PathVariable Long id, Authentication authentication) {
        boolean includeDisabled = securityService.isAdminIfAuthenticated(authentication);
        Workout workout = workoutService.getWorkoutById(id, includeDisabled);

        return ResponseEntity.ok()
                .body(WorkoutMapper.toWorkoutResponse(workout, fileStorageService, VALID_MINUTES));
    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Create workout",
            description = "Creates a new workout. Available to administrators only.")
    public ResponseEntity<WorkoutResponseDto> createWorkout(
            @Valid @RequestBody WorkoutRequestDto workoutRequest) {
        Workout createdWorkout =
                workoutService.createWorkout(WorkoutMapper.toEntity(workoutRequest));
        return ResponseEntity.ok()
                .body(
                        WorkoutMapper.toWorkoutResponse(
                                createdWorkout, fileStorageService, VALID_MINUTES));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Update workout",
            description = "Updates an existing workout. Available to administrators only.")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(
            @PathVariable Long id, @Valid @RequestBody WorkoutRequestDto workoutRequest) {
        Workout updatedWorkout =
                workoutService.updateWorkout(id, WorkoutMapper.toEntity(workoutRequest));
        return ResponseEntity.ok()
                .body(
                        WorkoutMapper.toWorkoutResponse(
                                updatedWorkout, fileStorageService, VALID_MINUTES));
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
        return ResponseEntity.ok()
                .body(
                        WorkoutMapper.toWorkoutResponse(
                                workoutService.setWorkoutEnabled(id, request.enabled()),
                                fileStorageService,
                                VALID_MINUTES));
    }

    @PostMapping("/{id}/start")
    @Operation(
            summary = "Start workout",
            description = "Starts a workout session for the authenticated user.")
    public ResponseEntity<WorkoutResponseDto> startWorkout(
            @PathVariable Long id, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        Workout workout = workoutService.startWorkout(id, userId);
        return ResponseEntity.ok()
                .body(WorkoutMapper.toWorkoutResponse(workout, fileStorageService, VALID_MINUTES));
    }
}
