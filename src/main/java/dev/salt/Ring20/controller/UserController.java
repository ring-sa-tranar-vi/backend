package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.callback.CallbackPreferenceRequestDto;
import dev.salt.Ring20.dto.callback.CallbackPreferenceResponseDto;
import dev.salt.Ring20.dto.event.EventResponseDto;
import dev.salt.Ring20.dto.fcmToken.FcmTokenRequestDto;
import dev.salt.Ring20.dto.organisation.OrganisationResponseDto;
import dev.salt.Ring20.dto.user.UserCreateRequestDto;
import dev.salt.Ring20.dto.user.UserRequestDto;
import dev.salt.Ring20.dto.user.UserResponseDto;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.enums.DayOfWeekType;
import dev.salt.Ring20.entity.enums.UserRole;
import dev.salt.Ring20.mapper.CallBackPreferenceMapper;
import dev.salt.Ring20.mapper.EventMapper;
import dev.salt.Ring20.mapper.OrganizationMapper;
import dev.salt.Ring20.mapper.UserMapper;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.UserService;
import dev.salt.Ring20.service.security.CurrentUserService;
import dev.salt.Ring20.service.security.DisplayResolverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description =
                "Endpoints for managing user profiles, preferences, progress, and personal data.")
public class UserController {

    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final DisplayResolverService displayResolverService;
    private final CurrentUserService currentUserService;

    public UserController(
            UserService userService,
            ActivityLogService activityLogService,
            DisplayResolverService displayResolverService,
            CurrentUserService currentUserService) {
        this.userService = userService;
        this.activityLogService = activityLogService;
        this.displayResolverService = displayResolverService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me/role")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my role", description = "Retrieves the current user's role.")
    public ResponseEntity<UserRole> getMyRole(Authentication authentication) {
        return ResponseEntity.ok().body(userService.getUserRole(authentication.getName()));
    }

    @PostMapping("/me/fcm-token")
    @Operation(
            summary = "Save FCM token",
            description = "Stores the user's FCM token for callback notifications.")
    public ResponseEntity<Void> saveFcmToken(
            Authentication authentication, @Valid @RequestBody FcmTokenRequestDto request) {

        Long userId = currentUserService.getCurrentUser(authentication).getId();
        userService.setFcmToken(userId, request.token());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/profile")
    @Operation(
            summary = "Get my profile",
            description = "Retrieves the profile of the authenticated user.")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile(Authentication authentication) {
        String clerkId = currentUserService.getClerkId(authentication);
        User currentUser = userService.getByClerkIdOrThrow(clerkId);
        boolean isAdmin = userService.isAdmin(clerkId);
        return ResponseEntity.ok().body(UserMapper.toResponse(currentUser, isAdmin));
    }

    @GetMapping("/by-clerk/{clerkId}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user by Clerk ID",
            description = "Retrieves a user using their Clerk ID.")
    public ResponseEntity<UserResponseDto> getUserByClerkId(
            @PathVariable String clerkId, Authentication authentication) {
        currentUserService.getJwtOrThrow(authentication);
        User user = userService.getByClerkIdOrThrow(clerkId);

        boolean isAdmin = userService.isAdmin(currentUserService.getClerkId(authentication));
        return ResponseEntity.ok().body(UserMapper.toResponse(user, isAdmin));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(summary = "Get user by ID", description = "Retrieves a user using their ID.")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(UserMapper.toResponse(user));
    }

    @PostMapping
    @Operation(
            summary = "Create user",
            description = "Creates a new user based on the authentication token.")
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody(required = false) UserCreateRequestDto request,
            Authentication authentication) {
        Jwt jwt = currentUserService.getJwtOrThrow(authentication);

        String requestedName = request != null ? request.displayName() : null;
        String displayName =
                requestedName != null && !requestedName.isBlank()
                        ? requestedName
                        : displayResolverService.resolveDisplayName(jwt);
        User created = userService.createUser(jwt.getSubject(), displayName);

        boolean isAdmin = userService.isAdmin(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(created, isAdmin));
    }

    @PutMapping("/me/profile")
    @Operation(
            summary = "Update my profile",
            description = "Updates the profile of the authenticated user.")
    public ResponseEntity<UserResponseDto> updateCurrentUserProfile(
            @Valid @RequestBody UserRequestDto userRequest, Authentication authentication) {
        User userToUpdate = UserMapper.toUserEntity(userRequest);
        String clerkId = currentUserService.getClerkId(authentication);
        User updated = userService.updateUserPreferencesByClerkId(clerkId, userToUpdate);

        boolean isAdmin = userService.isAdmin(clerkId);
        return ResponseEntity.ok().body(UserMapper.toResponse(updated, isAdmin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == @securityService.currentUserId(authentication.name)")
    @Operation(summary = "Update user preferences", description = "Updates a user's preferences.")
    public ResponseEntity<UserResponseDto> updateUserPreferences(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequest,
            Authentication authentication) {
        User userToUpdate = UserMapper.toUserEntity(userRequest);
        String clerkId = currentUserService.getClerkId(authentication);
        User updated = userService.updateUserPreferencesByClerkId(clerkId, userToUpdate);

        boolean isAdmin = userService.isAdmin(clerkId);
        return ResponseEntity.ok().body(UserMapper.toResponse(updated, isAdmin));
    }

    @GetMapping("/me/followed-orgs")
    @Operation(
            summary = "Get followed organisations",
            description = "Retrieves organisations followed by the authenticated user.")
    public ResponseEntity<List<OrganisationResponseDto>> getAllFollowedOrganization(
            Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);

        return ResponseEntity.ok()
                .body(
                        userService.getUserOrganizationById(currentUser.getId()).stream()
                                .map(OrganizationMapper::toResponseDto)
                                .toList());
    }

    @PostMapping("/me/followed-orgs/{orgId}")
    @Operation(
            summary = "Follow organisation",
            description = "Adds an organisation to the user's followed list.")
    public ResponseEntity<OrganisationResponseDto> followedOrg(
            Authentication authentication, @PathVariable Long orgId) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        Organisation org = userService.addFollowOrganization(currentUser.getId(), orgId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrganizationMapper.toResponseDto(org));
    }

    @DeleteMapping("/me/followed-orgs/{orgId}")
    @Operation(
            summary = "Unfollow organisation",
            description = "Removes an organisation from the user's followed list.")
    public ResponseEntity<Void> removeFollowedOrg(
            Authentication authentication, @PathVariable Long orgId) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        userService.removeFollowOrganization(currentUser.getId(), orgId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/attending-events")
    @Operation(
            summary = "Get attending events",
            description = "Retrieves events attended by the authenticated user.")
    public ResponseEntity<List<EventResponseDto>> getAllAttendingEvents(
            Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);

        return ResponseEntity.ok()
                .body(
                        userService.getUserEventsById(currentUser.getId()).stream()
                                .map(EventMapper::toEventResponseDto)
                                .toList());
    }

    @PostMapping("/me/attending-events/{eventId}")
    @Operation(
            summary = "Attend event",
            description = "Adds an event to the user's attended events.")
    public ResponseEntity<EventResponseDto> attendEvent(
            Authentication authentication, @PathVariable Long eventId) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        Event event = userService.addAttendEvent(currentUser.getId(), eventId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventMapper.toEventResponseDto(event));
    }

    @DeleteMapping("/me/attending-events/{eventId}")
    @Operation(
            summary = "Remove attending event",
            description = "Removes an event from the user's attended events.")
    public ResponseEntity<Void> removeAttendEvent(
            Authentication authentication, @PathVariable Long eventId) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        userService.removeAttendEvent(currentUser.getId(), eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/progress")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user progress",
            description = "Retrieves workout progress for a user.")
    public ResponseEntity<Map<String, Object>> getUserProgress(@PathVariable Long userId) {
        return ResponseEntity.ok().body(activityLogService.getUserProgress(userId));
    }

    @GetMapping("/me/progress")
    @Operation(
            summary = "Get my progress",
            description = "Retrieves workout progress for the authenticated user.")
    public ResponseEntity<Map<String, Object>> getMyProgress(Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);

        return ResponseEntity.ok().body(activityLogService.getUserProgress(currentUser.getId()));
    }

    @GetMapping("/{userId}/callback-preference")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Get callback preferences",
            description = "Retrieves callback preferences for a user.")
    public ResponseEntity<List<CallbackPreferenceResponseDto>> getAllCallbackPreference(
            @PathVariable Long userId) {
        return ResponseEntity.ok()
                .body(
                        userService.getCallbackPreferences(userId).stream()
                                .map(CallBackPreferenceMapper::toCallbackResponse)
                                .toList());
    }

    @PostMapping("/{userId}/callback-preference")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Add or update callback preference",
            description = "Creates or updates a user's callback preference.")
    public ResponseEntity<CallbackPreferenceResponseDto> addOrUpdateCallBackPreference(
            @PathVariable Long userId, @Valid @RequestBody CallbackPreferenceRequestDto request) {
        CallbackPreference saved =
                userService.addOrUpdateCallbackPreference(
                        userId, CallBackPreferenceMapper.toCallbackPreference(request));

        return ResponseEntity.ok().body(CallBackPreferenceMapper.toCallbackResponse(saved));
    }

    @DeleteMapping("/{userId}/callback-preference/{day}")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Remove callback preference",
            description = "Removes a user's callback preference for a specific day.")
    public ResponseEntity<Void> removeCallBackPreference(
            @PathVariable Long userId, @PathVariable DayOfWeekType day) {
        userService.removeCallbackPreference(userId, day);
        return ResponseEntity.noContent().build();
    }

    //    private User getCurrentUser(Authentication authentication) {
    //        return userService.getByClerkIdOrThrow(getClerkId(authentication));
    //    }
    //
    //    private String getClerkId(Authentication authentication) {
    //        return currentUserService.getJwtOrThrow(authentication).getSubject();
    //    }

    //    private Jwt getJwtOrThrow(Authentication authentication) {
    //        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
    //            throw new ResponseStatusException(
    //                    UNAUTHORIZED, "Missing or invalid authentication token");
    //        }
    //        return jwt;
    //    }
}
