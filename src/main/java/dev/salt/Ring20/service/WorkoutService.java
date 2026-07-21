package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.WorkoutResponseDto;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutService {

    private static final String STATUS_STARTED = "STARTED";
    private final WorkoutRepository workoutRepository;
    private final ActivityLogRepository activityLogRepository;

    public WorkoutService(
            WorkoutRepository workoutRepository, ActivityLogRepository activityLogRepository) {
        this.workoutRepository = workoutRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public String getWorkoutAudioUrl(Long id) {
        validateId(id);
        Workout workout =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Workout not found with id: " + id));

        if (workout.getWorkoutAudio() == null || workout.getWorkoutAudio().isBlank()) {
            throw new NoSuchElementException("Workout audio not found with id: " + id);
        }

        return workout.getWorkoutAudio();
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponseDto> getAllWorkouts(boolean includeDisabled) {
        List<Workout> workouts =
                includeDisabled
                        ? workoutRepository.findAll()
                        : workoutRepository.findByEnabledTrue();

        return workouts.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public WorkoutResponseDto getWorkoutById(Long id, boolean includeDisabled) {
        validateId(id);
        Workout workout =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Workout not found with id: " + id));

        if (!includeDisabled && Boolean.FALSE.equals(workout.getEnabled())) {
            throw new NoSuchElementException("Workout not found with id: " + id);
        }

        return mapToResponse(workout);
    }

    @Transactional
    public WorkoutResponseDto startWorkout(Long id, Long userId) {
        // Reuse the logic from getWorkoutById to ensure mapping and session handling
        WorkoutResponseDto workout = getWorkoutById(id, false);

        if (userId != null) {
            boolean alreadyStarted =
                    activityLogRepository.existsByUserIdAndWorkoutIdAndStatus(
                            userId, id, STATUS_STARTED);

            if (!alreadyStarted) {
                ActivityLog activityLog = new ActivityLog();
                activityLog.setUserId(userId);
                activityLog.setWorkoutId(workout.id()); // Record uses accessor style
                activityLog.setStatus(STATUS_STARTED);
                activityLog.setCompletedAt(LocalDateTime.now());
                activityLogRepository.save(activityLog);
            }
        }
        return workout;
    }

    @Transactional
    public WorkoutResponseDto createWorkout(Workout workout) {
        validateWorkoutForWrite(workout);
        Workout saved = workoutRepository.save(workout);
        return mapToResponse(saved);
    }

    @Transactional
    public WorkoutResponseDto updateWorkout(Long id, Workout workout) {
        validateId(id);
        validateWorkoutForWrite(workout);

        Workout existing =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Workout not found with id: " + id));

        existing.setName(workout.getName());
        existing.setDescription(workout.getDescription());
        existing.setDashboardName(workout.getDashboardName());
        existing.setDashboardDescription(workout.getDashboardDescription());
        existing.setSubtitleText(workout.getSubtitleText());
        existing.setInstructionsSubtitleText(workout.getInstructionsSubtitleText());
        existing.setLevel(workout.getLevel());
        existing.setType(workout.getType());
        existing.setDurationSeconds(workout.getDurationSeconds());
        existing.setInstructionsAudio(workout.getInstructionsAudio());
        existing.setWorkoutAudio(workout.getWorkoutAudio());
        existing.setInstructionsImage(workout.getInstructionsImage());
        existing.setWorkoutImage(workout.getWorkoutImage());
        existing.setInstructionsVideo(workout.getInstructionsVideo());
        existing.setInstructionsVideoStart(workout.getInstructionsVideoStart());
        existing.setInstructionsVideoStop(workout.getInstructionsVideoStop());
        existing.setKneeFriendly(workout.getKneeFriendly());
        existing.setLowImpact(workout.getLowImpact());
        existing.setSeated(workout.getSeated());
        existing.setBeginnerFriendly(workout.getBeginnerFriendly());
        if (workout.getTrainer() != null) {
            existing.setTrainer(workout.getTrainer());
        }

        return mapToResponse(workoutRepository.save(existing));
    }

    @Transactional
    public WorkoutResponseDto setWorkoutEnabled(Long id, boolean enabled) {
        validateId(id);

        Workout existing =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Workout not found with id: " + id));

        existing.setEnabled(enabled);
        Workout updated = workoutRepository.save(existing);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteWorkout(Long id) {
        validateId(id);

        if (!workoutRepository.existsById(id)) {
            throw new NoSuchElementException("Workout not found with id: " + id);
        }

        workoutRepository.deleteById(id);
    }

    private WorkoutResponseDto mapToResponse(Workout workout) {
        WorkoutResponseDto.TrainerIdDTO trainerDTO = null;

        // This is where the @Transactional is key:
        // It keeps the session open to check if a trainer exists and get their ID.
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

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be a positive number.");
        }
    }

    private void validateWorkoutForWrite(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout body is required.");
        }

        if (workout.getName() == null || workout.getName().isBlank()) {
            throw new IllegalArgumentException("Workout name is required.");
        }

        if (workout.getEnabled() == null) {
            workout.setEnabled(true);
        }

        Integer durationSeconds = workout.getDurationSeconds();
        if (durationSeconds != null && durationSeconds < 0) {
            throw new IllegalArgumentException("DurationSeconds cannot be negative");
        }
    }
}
