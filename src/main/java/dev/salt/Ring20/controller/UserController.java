package dev.salt.Ring20.controller;

import dev.salt.Ring20.dto.*;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import dev.salt.Ring20.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "Endpoints for managing user profiles, preferences, progress, and personal data."
)
public class UserController {

    private static final String DEFAULT_DISPLAY_NAME = "No name entered";
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final OrganisationService organisationService;
    private final EventService eventService;

    public UserController(
            UserService userService,
            ActivityLogService activityLogService,
            OrganisationService organisationService,
            EventService eventService) {
        this.userService = userService;
        this.activityLogService = activityLogService;
        this.organisationService = organisationService;
        this.eventService = eventService;
    }

    @GetMapping("/me/role")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my role",
            description = "Retrieves the current user's role."
    )
    public ResponseEntity<UserRole> getMyRole(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserRole(authentication.getName()));
    }

    @PostMapping("/me/fcm-token")
    @Operation(
            summary = "Save FCM token",
            description = "Stores the user's FCM token for callback notifications."
    )
    public ResponseEntity<Void> saveFcmToken(
            Authentication authentication, @Valid @RequestBody FcmTokenRequestDto request) {

        Long userId = getCurrentUser(authentication).getId();
        userService.setFcmToken(userId, request.token());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/profile")
    @Operation(
            summary = "Get my profile",
            description = "Retrieves the profile of the authenticated user."
    )
    public ResponseEntity<UserResponseDto> getCurrentUserProfile(Authentication authentication) {
        User currentUser = userService.getByClerkIdOrThrow(getClerkId(authentication));

        return ResponseEntity.ok(toResponse(currentUser, getClerkId(authentication)));
    }

    @GetMapping("/by-clerk/{clerkId}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user by Clerk ID",
            description = "Retrieves a user using their Clerk ID."
    )
    public ResponseEntity<UserResponseDto> getUserByClerkId(
            @PathVariable String clerkId, Authentication authentication) {
        getJwtOrThrow(authentication);
        User user = userService.getByClerkIdOrThrow(clerkId);

        return ResponseEntity.ok(toResponse(user, clerkId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user using their ID."
    )
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping
    @Operation(
            summary = "Create user",
            description = "Creates a new user based on the authentication token."
    )
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody(required = false) UserCreateRequestDto request,
            Authentication authentication) {
        Jwt jwt = getJwtOrThrow(authentication);
        String requestedName = request != null ? request.displayName() : null;

        User created =
                userService.createUser(
                        jwt.getSubject(),
                        requestedName != null && !requestedName.isBlank()
                                ? requestedName
                                : resolveDisplayName(jwt));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(created, jwt.getSubject()));
    }

    @PutMapping("/me/profile")
    @Operation(
            summary = "Update my profile",
            description = "Updates the profile of the authenticated user."
    )
    public ResponseEntity<UserResponseDto> updateCurrentUserProfile(
            @Valid @RequestBody UserRequestDto userRequest, Authentication authentication) {
        User updated =
                userService.updateUserPreferencesByClerkId(
                        getClerkId(authentication),
                        userRequest.name(),
                        userRequest.intensityLevel(),
                        userRequest.context(),
                        userRequest.trainerId(),
                        userRequest.city(),
                        userRequest.onboarding());

        return ResponseEntity.ok(toResponse(updated, getClerkId(authentication)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Update user preferences",
            description = "Updates a user's preferences."
    )
    public ResponseEntity<UserResponseDto> updateUserPreferences(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequest,
            Authentication authentication) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();

        User updated =
                userService.updateUserPreferencesByClerkId(
                        getClerkId(authentication),
                        userRequest.name(),
                        userRequest.intensityLevel(),
                        userRequest.context(),
                        userRequest.trainerId(),
                        userRequest.city(),
                        userRequest.onboarding());

        return ResponseEntity.ok(toResponse(updated, getClerkId(authentication)));
    }

    @GetMapping("/me/followed-orgs")
    @Operation(
            summary = "Get followed organisations",
            description = "Retrieves organisations followed by the authenticated user."
    )
    public ResponseEntity<List<OrganisationResponseDto>> getAllFollowedOrgs(
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return ResponseEntity.ok(
                userService.getUserOrgsById(currentUser.getId()).stream()
                        .map(this::toOrgResponseDto)
                        .toList());
    }

    @PostMapping("/me/followed-orgs/{orgId}")
    @Operation(
            summary = "Follow organisation",
            description = "Adds an organisation to the user's followed list."
    )
    public ResponseEntity<UserResponseDto> followedOrg(
            Authentication authentication, @PathVariable Long orgId) {
        User currentUser = getCurrentUser(authentication);
        User updated =
                userService.addFollowOrganization(
                        currentUser.getId(), organisationService.getOrganisationById(orgId));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(updated));
    }

    @DeleteMapping("/me/followed-orgs/{orgId}")
    @Operation(
            summary = "Unfollow organisation",
            description = "Removes an organisation from the user's followed list."
    )
    public ResponseEntity<UserResponseDto> removeFollowedOrg(
            Authentication authentication, @PathVariable Long orgId) {
        User currentUser = getCurrentUser(authentication);
        User updated =
                userService.removeFollowOrganization(
                        currentUser.getId(), organisationService.getOrganisationById(orgId));
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/me/attending-events")
    @Operation(
            summary = "Get attending events",
            description = "Retrieves events attended by the authenticated user."
    )
    public ResponseEntity<List<EventResponseDto>> getAllAttendingEvents(
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return ResponseEntity.ok(
                userService.getUserEventsById(currentUser.getId()).stream()
                        .map(this::toEventResponseDto)
                        .toList());
    }

    @PostMapping("/me/attending-events/{eventId}")
    @Operation(
            summary = "Attend event",
            description = "Adds an event to the user's attended events."
    )
    public ResponseEntity<UserResponseDto> attendEvent(
            Authentication authentication, @PathVariable Long eventId) {
        User currentUser = getCurrentUser(authentication);
        User updated =
                userService.addAttendEvent(currentUser.getId(), eventService.getEventById(eventId));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(updated));
    }

    @DeleteMapping("/me/attending-events/{eventId}")
    @Operation(
            summary = "Remove attending event",
            description = "Removes an event from the user's attended events."
    )
    public ResponseEntity<UserResponseDto> removeAttendEvent(
            Authentication authentication, @PathVariable Long eventId) {
        User currentUser = getCurrentUser(authentication);
        User updated =
                userService.removeAttendEvent(
                        currentUser.getId(), eventService.getEventById(eventId));
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/{userId}/progress")
    @PreAuthorize("@securityService.isAdmin(authentication.name)")
    @Operation(
            summary = "Get user progress",
            description = "Retrieves workout progress for a user."
    )
    public ResponseEntity<Map<String, Object>> getUserProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getUserProgress(userId));
    }

    @GetMapping("/me/progress")
    @Operation(
            summary = "Get my progress",
            description = "Retrieves workout progress for the authenticated user."
    )
    public ResponseEntity<Map<String, Object>> getMyProgress(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return ResponseEntity.ok(activityLogService.getUserProgress(currentUser.getId()));
    }

    @GetMapping("/{userId}/callback-preference")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Get callback preferences",
            description = "Retrieves callback preferences for a user."
    )
    public List<CallbackPreference> getAll(@PathVariable Long userId) {
        return userService.getUserById(userId).getCallbackPreferences();
    }

    @PostMapping("/{userId}/callback-preference")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Add or update callback preference",
            description = "Creates or updates a user's callback preference."
    )
    public UserResponseDto addOrUpdate(
            @PathVariable Long userId, @Valid @RequestBody CallbackPreference callback) {
        return toResponse(userService.addOrUpdateCallbackPreference(userId, callback));
    }

    @DeleteMapping("/{userId}/callback-preference/{day}")
    @PreAuthorize("#userId == @securityService.currentUserId(authentication.name)")
    @Operation(
            summary = "Remove callback preference",
            description = "Removes a user's callback preference for a specific day."
    )
    public UserResponseDto remove(@PathVariable Long userId, @PathVariable DayOfWeekType day) {
        return toResponse(userService.removeCallbackPreference(userId, day));
    }


    private User getCurrentUser(Authentication authentication) {
        return userService.findByClerkId(getClerkId(authentication)).orElseThrow();
    }

    private String getClerkId(Authentication authentication) {
        return getJwtOrThrow(authentication).getSubject();
    }

    private Jwt getJwtOrThrow(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt;
    }

    private String resolveDisplayName(Jwt jwt) {
        // Try common claim keys that Clerk/OpenID might provide for a user's name.
        String[] claimKeys = new String[]{"name", "full_name", "preferred_username"};
        for (String key : claimKeys) {
            Object claimVal = jwt.getClaims().get(key);
            if (claimVal instanceof String) {
                String s = ((String) claimVal).trim();
                if (!s.isEmpty()) return s;
            }
        }

        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");
        String fullName =
                String.join(
                                " ",
                                Stream.of(givenName, familyName)
                                        .filter(part -> part != null && !part.isBlank())
                                        .toList())
                        .trim();

        if (!fullName.isEmpty()) {
            return fullName;
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email.trim();
        }

        // If Clerk does not provide a name claim, store a readable placeholder.
        return DEFAULT_DISPLAY_NAME;
    }

    private UserResponseDto toResponse(User user, String clerkId) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getIntensityLevel(),
                user.getContext(),
                userService.isAdmin(clerkId),
                user.getTrainerId(),
                user.getCity(),
                user.isOnboarding());
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getIntensityLevel(),
                user.getContext(),
                "ADMIN".equals(user.getRole()),
                user.getTrainerId(),
                user.getCity(),
                user.isOnboarding());
    }

    private EventResponseDto toEventResponseDto(Event event) {
        Long organisationId =
                event.getOrganisation() == null ? null : event.getOrganisation().getId();
        return new EventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                organisationId,
                event.getCity(),
                event.getVenue(),
                event.getEventType());
    }

    private OrganisationResponseDto toOrgResponseDto(Organisation organisation) {
        List<EventResponseDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream().map(this::toEventResponseDto).toList();
        return new OrganisationResponseDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events,
                organisation.getOrgCity());
    }
}
