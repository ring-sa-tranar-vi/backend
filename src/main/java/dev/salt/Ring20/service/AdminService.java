package dev.salt.Ring20.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import dev.salt.Ring20.dto.AdminEventRequestDTO;
import dev.salt.Ring20.dto.AdminEventResponseDTO;
import dev.salt.Ring20.dto.AdminOrganisationRequestDTO;
import dev.salt.Ring20.dto.AdminOrganisationResponseDTO;
import dev.salt.Ring20.dto.AdminRecentActivityDTO;
import dev.salt.Ring20.dto.AdminTrainerOverviewDTO;
import dev.salt.Ring20.dto.AdminUserSummaryDTO;
import dev.salt.Ring20.dto.AdminWorkoutUsageDTO;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.EventRepository;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.UserRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final WorkoutRepository workoutRepository;
    private final TrainerRepository trainerRepository;
    private final OrganisationRepository organisationRepository;
    private final EventRepository eventRepository;

    public AdminService(
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository,
            WorkoutRepository workoutRepository,
            TrainerRepository trainerRepository,
            OrganisationRepository organisationRepository,
            EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.workoutRepository = workoutRepository;
        this.trainerRepository = trainerRepository;
        this.organisationRepository = organisationRepository;
        this.eventRepository = eventRepository;
    }

    public List<AdminUserSummaryDTO> getUserSummaries() {
        List<User> users = userRepository.findAll();
        Map<Long, LocalDateTime> lastCompletedAtByUserId = new HashMap<>();

        for (ActivityLog activityLog : activityLogRepository.findAll()) {
            if (!"COMPLETED".equalsIgnoreCase(activityLog.getStatus())
                    || activityLog.getUserId() == null
                    || activityLog.getCompletedAt() == null) {
                continue;
            }

            lastCompletedAtByUserId.merge(
                    activityLog.getUserId(),
                    activityLog.getCompletedAt(),
                    (current, candidate) ->
                            candidate.isAfter(current) ? candidate : current);
        }

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .map(
                        user ->
                                new AdminUserSummaryDTO(
                                        user.getId(),
                                        user.getName(),
                                        user.getClerkId(),
                                        user.getRole(),
                                        user.getIntensityLevel(),
                                        user.getTrainerId(),
                                        lastCompletedAtByUserId.get(user.getId())))
                .toList();
    }

    public List<AdminRecentActivityDTO> getRecentActivityLogs() {
        Map<Long, String> userNameById = new HashMap<>();
        for (User user : userRepository.findAll()) {
            userNameById.put(user.getId(), user.getName());
        }

        Map<Long, String> workoutNameById = new HashMap<>();
        for (Workout workout : workoutRepository.findAll()) {
            workoutNameById.put(workout.getId(), workout.getName());
        }

        return activityLogRepository.findAll().stream()
                .sorted(
                        Comparator.comparing(
                                        ActivityLog::getCompletedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(ActivityLog::getId, Comparator.reverseOrder()))
                .limit(25)
                .map(
                        activityLog ->
                                new AdminRecentActivityDTO(
                                        activityLog.getId(),
                                        activityLog.getUserId(),
                                        userNameById.getOrDefault(
                                                activityLog.getUserId(), "Unknown user"),
                                        activityLog.getWorkoutId(),
                                        workoutNameById.getOrDefault(
                                                activityLog.getWorkoutId(), "Unknown workout"),
                                        activityLog.getStatus(),
                                        activityLog.getDurationSeconds(),
                                        activityLog.getCompletedAt()))
                .toList();
    }

    public List<AdminWorkoutUsageDTO> getWorkoutUsage() {
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        Map<Long, Long> startedCountByWorkoutId = new HashMap<>();
        Map<Long, Long> completedCountByWorkoutId = new HashMap<>();
        Map<Long, LocalDateTime> lastCompletedAtByWorkoutId = new HashMap<>();

        for (ActivityLog activityLog : activityLogs) {
            Long workoutId = activityLog.getWorkoutId();
            if (workoutId == null) {
                continue;
            }

            startedCountByWorkoutId.merge(workoutId, 1L, Long::sum);

            if ("COMPLETED".equalsIgnoreCase(activityLog.getStatus())) {
                completedCountByWorkoutId.merge(workoutId, 1L, Long::sum);
                if (activityLog.getCompletedAt() != null) {
                    lastCompletedAtByWorkoutId.merge(
                            workoutId,
                            activityLog.getCompletedAt(),
                            (current, candidate) ->
                                    candidate.isAfter(current) ? candidate : current);
                }
            }
        }

        return workoutRepository.findAll().stream()
                .sorted(Comparator.comparing(Workout::getId))
                .map(
                        workout ->
                                new AdminWorkoutUsageDTO(
                                        workout.getId(),
                                        workout.getName(),
                                        workout.getTrainer() == null ? null : workout.getTrainer().getName(),
                                        startedCountByWorkoutId.getOrDefault(workout.getId(), 0L),
                                        completedCountByWorkoutId.getOrDefault(workout.getId(), 0L),
                                        lastCompletedAtByWorkoutId.get(workout.getId())))
                .toList();
    }

    public List<AdminTrainerOverviewDTO> getTrainerOverview() {
        Map<Long, Long> assignedUserCountByTrainerId = new HashMap<>();
        for (User user : userRepository.findAll()) {
            if (user.getTrainerId() != null) {
                assignedUserCountByTrainerId.merge(user.getTrainerId(), 1L, Long::sum);
            }
        }

        Map<Long, Long> workoutCountByTrainerId = new HashMap<>();
        Map<Long, Long> enabledWorkoutCountByTrainerId = new HashMap<>();
        for (Workout workout : workoutRepository.findAll()) {
            if (workout.getTrainer() == null || workout.getTrainer().getId() == null) {
                continue;
            }

            Long trainerId = workout.getTrainer().getId();
            workoutCountByTrainerId.merge(trainerId, 1L, Long::sum);
            if (Boolean.TRUE.equals(workout.getEnabled())) {
                enabledWorkoutCountByTrainerId.merge(trainerId, 1L, Long::sum);
            }
        }

        return trainerRepository.findAll().stream()
                .sorted(Comparator.comparing(Trainer::getId))
                .map(
                        trainer ->
                                new AdminTrainerOverviewDTO(
                                        trainer.getId(),
                                        trainer.getName(),
                                        trainer.getLanguage(),
                                        assignedUserCountByTrainerId.getOrDefault(trainer.getId(), 0L),
                                        workoutCountByTrainerId.getOrDefault(trainer.getId(), 0L),
                                        enabledWorkoutCountByTrainerId.getOrDefault(trainer.getId(), 0L)))
                .toList();
    }

    public User updateUser(Long id, User updateData) {
        User existing =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND,
                                                "User not found"));

        if (updateData.getName() != null && !updateData.getName().isBlank()) {
            existing.setName(updateData.getName());
        }
        if (updateData.getIntensityLevel() != null) {
            existing.setIntensityLevel(updateData.getIntensityLevel());
        }
        if (updateData.getContext() != null) {
            existing.setContext(updateData.getContext());
        }
        if (updateData.getTrainerId() != null) {
            existing.setTrainerId(updateData.getTrainerId());
        }
        if (updateData.getRole() != null && !updateData.getRole().isBlank()) {
            existing.setRole(updateData.getRole());
        }

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public List<AdminOrganisationResponseDTO> getOrganisations() {
        return organisationRepository.findAll().stream()
                .sorted(Comparator.comparing(Organisation::getId))
                .map(org -> new AdminOrganisationResponseDTO(org.getId(), org.getName(), org.getDescription()))
                .toList();
    }

    public AdminOrganisationResponseDTO createOrganisation(AdminOrganisationRequestDTO request) {
        if (request == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Request body is required");
        }

        String name = normalizeRequired(request.name(), 150);
        String description = normalizeOptional(request.description(), 2048);

        if (organisationRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(CONFLICT, "Organisation already exists");
        }

        Organisation organisation = new Organisation();
        organisation.setName(name);
        organisation.setDescription(description);

        Organisation saved = organisationRepository.save(organisation);
        return new AdminOrganisationResponseDTO(saved.getId(), saved.getName(), saved.getDescription());
    }

    public void deleteOrganisation(Long id) {
        validateId(id);

        if (!organisationRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Organisation not found");
        }

        eventRepository.deleteByOrganisationId(id);
        organisationRepository.deleteById(id);
    }

    public List<AdminEventResponseDTO> getEvents() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toAdminEventResponse)
                .toList();
    }

    public AdminEventResponseDTO createEvent(AdminEventRequestDTO request) {
        if (request == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Request body is required");
        }

        String name = normalizeRequired(request.name(), 180);
        String description = normalizeOptional(request.description(), 4096);
        validateId(request.organisationId());

        if (request.time() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "time is required");
        }

        Organisation organisation =
                organisationRepository
                        .findById(request.organisationId())
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Organisation not found"));

        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setTime(request.time());
        event.setOrganisation(organisation);

        Event saved = eventRepository.save(event);
        return toAdminEventResponse(saved);
    }

    public void deleteEvent(Long id) {
        validateId(id);

        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Event not found");
        }

        eventRepository.deleteById(id);
    }

    private AdminEventResponseDTO toAdminEventResponse(Event event) {
        Organisation organisation = event.getOrganisation();
        return new AdminEventResponseDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                organisation == null ? null : organisation.getId(),
                organisation == null ? null : organisation.getName());
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "id must be a positive number");
        }
    }

    private String normalizeRequired(String value, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "name is required");
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new ResponseStatusException(BAD_REQUEST, "name exceeds max length " + maxLength);
        }

        return normalized;
    }

    private String normalizeOptional(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > maxLength) {
            throw new ResponseStatusException(BAD_REQUEST, "description exceeds max length " + maxLength);
        }

        return normalized;
    }
}
