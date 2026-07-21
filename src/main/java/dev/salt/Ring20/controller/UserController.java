package dev.salt.Ring20.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dev.salt.Ring20.dto.*;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.service.ActivityLogService;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import dev.salt.Ring20.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
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

    private Jwt getJwtOrThrow(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED, "Missing or invalid authentication token");
        }
        return jwt;
    }

    private String getClerkId(Authentication authentication) {
        return getJwtOrThrow(authentication).getSubject();
    }

    private String resolveDisplayName(Jwt jwt) {
        // Try common claim keys that Clerk/OpenID might provide for a user's name.
        String[] claimKeys = new String[] {"name", "full_name", "preferred_username"};
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
                user.getCity());
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getIntensityLevel(),
                user.getContext(),
                "ADMIN".equals(user.getRole()),
                user.getTrainerId(),
                user.getCity());
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody(required = false) UserCreateRequestDto request,
            Authentication authentication) {
        Jwt jwt = getJwtOrThrow(authentication);
        String requestedName = request != null ? request.displayName() : null;

        User created =
                userService.createUser(
                        jwt.getSubject(),
                        requestedName != null && !requestedName.isBlank()
                                ? requestedName
                                : resolveDisplayName(jwt));

        return ResponseEntity.ok(toResponse(created, jwt.getSubject()));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile(Authentication authentication) {
        String clerkId = getClerkId(authentication);
        User currentUser = userService.getByClerkIdOrThrow(clerkId);

        return ResponseEntity.ok(toResponse(currentUser, clerkId));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserResponseDto> updateCurrentUserProfile(
            @RequestBody UserRequestDto userRequest, Authentication authentication) {
        String clerkId = getClerkId(authentication);
        User updated =
                userService.updateUserPreferencesByClerkId(
                        clerkId,
                        userRequest.name(),
                        userRequest.intensityLevel(),
                        userRequest.context(),
                        userRequest.trainerId(),
                        userRequest.city());

        return ResponseEntity.ok(toResponse(updated, clerkId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserPreferences(
            @PathVariable Long id,
            @RequestBody UserRequestDto userRequest,
            Authentication authentication) {
        String clerkId = getClerkId(authentication);
        User currentUser = userService.findByClerkId(clerkId).orElseThrow();

        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).build();
        }

        User updated =
                userService.updateUserPreferencesByClerkId(
                        clerkId,
                        userRequest.name(),
                        userRequest.intensityLevel(),
                        userRequest.context(),
                        userRequest.trainerId(),
                        userRequest.city());

        return ResponseEntity.ok(toResponse(updated, clerkId));
    }

    @GetMapping("/me/progress")
    public ResponseEntity<Map<String, Object>> getMyProgress(Authentication authentication) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();

        return ResponseEntity.ok(activityLogService.getUserProgress(currentUser.getId()));
    }

    @GetMapping("/me/followedOrg")
    public ResponseEntity<List<OrganisationResponseDto>> getAllFollowedOrgs(
            Authentication authentication) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();

        return ResponseEntity.ok(
                userService.getUserOrgsById(currentUser.getId()).stream()
                        .map(this::toOrgResponseDto)
                        .toList());
    }

    @GetMapping("/me/attendingEvent")
    public ResponseEntity<List<EventResponseDto>> getAllAttendingEvents(
            Authentication authentication) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();

        return ResponseEntity.ok(
                userService.getUserEventsById(currentUser.getId()).stream()
                        .map(this::toEventResponseDto)
                        .toList());
    }

    @PostMapping("/me/followedOrg/{orgId}")
    public ResponseEntity<UserResponseDto> followedOrg(
            Authentication authentication, @PathVariable Long orgId) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();
        User updated =
                userService.addFollowOrganization(
                        currentUser.getId(), organisationService.getOrganisationById(orgId));
        return ResponseEntity.status(201).body(toResponse(updated));
    }

    @PostMapping("/me/attendingEvent/{eventId}")
    public ResponseEntity<UserResponseDto> attendEvent(
            Authentication authentication, @PathVariable Long eventId) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();
        User updated =
                userService.addAttendEvent(currentUser.getId(), eventService.getEventById(eventId));
        return ResponseEntity.status(201).body(toResponse(updated));
    }

    @DeleteMapping("/me/followedOrg/{orgId}")
    public ResponseEntity<UserResponseDto> removeFollowedOrg(
            Authentication authentication, @PathVariable Long orgId) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();
        User updated =
                userService.removeFollowOrganization(
                        currentUser.getId(), organisationService.getOrganisationById(orgId));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/me/attendingEvent/{eventId}")
    public ResponseEntity<UserResponseDto> removeAttendEvent(
            Authentication authentication, @PathVariable Long eventId) {
        User currentUser = userService.findByClerkId(getClerkId(authentication)).orElseThrow();
        User updated =
                userService.removeAttendEvent(
                        currentUser.getId(), eventService.getEventById(eventId));
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/by-clerk/{clerkId}")
    public ResponseEntity<UserResponseDto> getUserByClerkId(
            @PathVariable String clerkId, Authentication authentication) {
        getJwtOrThrow(authentication);
        User user = userService.getByClerkIdOrThrow(clerkId);

        return ResponseEntity.ok(toResponse(user, clerkId));
    }

    @GetMapping("/{userId}/progress")
    public ResponseEntity<Map<String, Object>> getUserProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getUserProgress(userId));
    }

    @GetMapping("/{userId}/callback-preferance")
    public List<CallbackPreference> getAll(@PathVariable Long userId) {
        return userService.getUserById(userId).getCallbackPreferences();
    }

    @PostMapping("/{userId}/callback-preferance")
    public User addOrUpdate(@PathVariable Long userId, @RequestBody CallbackPreference callback) {
        return userService.addOrUpdateCallbackPreference(userId, callback);
    }

    @DeleteMapping("/{userId}/callback-preferance/{day}")
    public User remove(@PathVariable Long userId, @PathVariable DayOfWeekType day) {
        return userService.removeCallbackPreference(userId, day);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toResponse(user));
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
