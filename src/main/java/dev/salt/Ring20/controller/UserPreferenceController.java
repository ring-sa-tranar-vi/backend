package dev.salt.Ring20.controller;

import dev.salt.Ring20.entity.enums.UserWorkoutPreferenceType;
import dev.salt.Ring20.service.UserWorkoutPreferenceService;
import dev.salt.Ring20.service.security.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/preferences")
@Tag(
        name = "User Preferences",
        description = "Endpoints for managing user workout preferences and favorites.")
public class UserPreferenceController {
    //TODO: use the same way of sending ResponseEntity, either .ok(whats in the body) or .ok().body(whats in the body) not both
    //TODO: empty line between grouped fields

    private final CurrentUserService currentUserService;
    private final UserWorkoutPreferenceService preferenceService;

    public UserPreferenceController(
            CurrentUserService currentUserService, UserWorkoutPreferenceService preferenceService) {
        this.currentUserService = currentUserService;
        this.preferenceService = preferenceService;
    }

    @GetMapping
    @Operation(
            summary = "Get my preferences",
            description = "Retrieves workout preferences for the authenticated user.")
    public ResponseEntity<Map<String, List<Long>>> getMyPreferences(Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        return ResponseEntity.ok(preferenceService.getPreferences(userId));
    }

    @PostMapping("/favorites/{workoutId}")
    @Operation(
            summary = "Add favorite workout",
            description = "Adds a workout to the user's favorites list.")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.addPreference(userId, workoutId, UserWorkoutPreferenceType.FAVORITE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/favorites/{workoutId}")
    @Operation(
            summary = "Remove favorite workout",
            description = "Removes a workout from the user's favorites list.")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.removePreference(userId, workoutId, UserWorkoutPreferenceType.FAVORITE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/disliked/{workoutId}")
    @Operation(
            summary = "Add disliked workout",
            description = "Adds a workout to the user's disliked list.")
    public ResponseEntity<Void> addDisliked(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.addPreference(userId, workoutId, UserWorkoutPreferenceType.DISLIKED);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/disliked/{workoutId}")
    @Operation(
            summary = "Remove disliked workout",
            description = "Removes a workout from the user's disliked list.")
    public ResponseEntity<Void> removeDisliked(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.removePreference(userId, workoutId, UserWorkoutPreferenceType.DISLIKED);
        return ResponseEntity.noContent().build();
    }
}
