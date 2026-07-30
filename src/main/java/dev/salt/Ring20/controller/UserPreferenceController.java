package dev.salt.Ring20.controller;

import dev.salt.Ring20.entity.UserWorkoutPreferenceType;
import dev.salt.Ring20.service.UserWorkoutPreferenceService;
import dev.salt.Ring20.service.security.CurrentUserService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/preferences")
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
    public ResponseEntity<Map<String, List<Long>>> getMyPreferences(Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        return ResponseEntity.ok(preferenceService.getPreferences(userId));
    }

    @PostMapping("/favorites/{workoutId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.addPreference(userId, workoutId, UserWorkoutPreferenceType.FAVORITE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/favorites/{workoutId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.removePreference(userId, workoutId, UserWorkoutPreferenceType.FAVORITE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/disliked/{workoutId}")
    public ResponseEntity<Void> addDisliked(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.addPreference(userId, workoutId, UserWorkoutPreferenceType.DISLIKED);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/disliked/{workoutId}")
    public ResponseEntity<Void> removeDisliked(
            @PathVariable Long workoutId, Authentication authentication) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        preferenceService.removePreference(userId, workoutId, UserWorkoutPreferenceType.DISLIKED);
        return ResponseEntity.noContent().build();
    }
}
