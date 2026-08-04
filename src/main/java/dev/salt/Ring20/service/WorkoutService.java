package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.TrainerRepository;
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
    private final TrainerRepository trainerRepository;

    public WorkoutService(
            WorkoutRepository workoutRepository,
            ActivityLogRepository activityLogRepository,
            TrainerRepository trainerRepository) {
        this.workoutRepository = workoutRepository;
        this.activityLogRepository = activityLogRepository;
        this.trainerRepository = trainerRepository;
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
    public List<Workout> getAllWorkouts(boolean includeDisabled) {
        if (includeDisabled) {
            return workoutRepository.findAll();
        }

        return workoutRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    public Workout getWorkoutById(Long id, boolean includeDisabled) {
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

        return workout;
    }

    @Transactional
    public Workout startWorkout(Long id, Long userId) {

        Workout workout = getWorkoutById(id, false);

        if (userId != null) {
            boolean alreadyStarted =
                    activityLogRepository.existsByUserIdAndWorkoutIdAndStatus(
                            userId, id, STATUS_STARTED);

            if (!alreadyStarted) {
                ActivityLog activityLog = new ActivityLog();
                activityLog.setUserId(userId);
                activityLog.setWorkoutId(workout.getId());
                activityLog.setStatus(STATUS_STARTED);
                activityLog.setCompletedAt(LocalDateTime.now());
                activityLogRepository.save(activityLog);
            }
        }
        return workout;
    }

    @Transactional
    public Workout createWorkout(Workout workout) {
        validateWorkoutNotNull(workout);
        validateName(workout);
        validateDuration(workout);
        applyDefaults(workout);
        var trainer = resolveTrainer(workout);
        workout.setTrainer(trainer);
        return workoutRepository.save(workout);
    }

    @Transactional
    public Workout updateWorkout(Long id, Workout workout) {
        validateId(id);
        validateWorkoutNotNull(workout);
        validateName(workout);
        validateDuration(workout);
        applyDefaults(workout);
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
        if (workout.getTrainer() != null && workout.getTrainer().getId() != null) {
            existing.setTrainer(resolveTrainer(workout));
        }

        return workoutRepository.save(existing);
    }

    @Transactional
    public Workout setWorkoutEnabled(Long id, boolean enabled) {
        validateId(id);

        Workout existing =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Workout not found with id: " + id));

        existing.setEnabled(enabled);
        return workoutRepository.save(existing);
    }

    @Transactional
    public void deleteWorkout(Long id) {
        validateId(id);

        if (!workoutRepository.existsById(id)) {
            throw new NoSuchElementException("Workout not found with id: " + id);
        }

        workoutRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be a positive number.");
        }
    }

    private void validateWorkoutNotNull(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout body is required.");
        }
    }

    private void validateName(Workout workout) {
        if (workout.getName() == null || workout.getName().isBlank()) {
            throw new IllegalArgumentException("Workout name is required.");
        }
    }

    private void validateDuration(Workout workout) {
        Integer duration = workout.getDurationSeconds();
        if (duration != null && duration < 0) {
            throw new IllegalArgumentException("DurationSeconds cannot be negative");
        }
    }

    private void applyDefaults(Workout workout) {
        if (workout.getEnabled() == null) {
            workout.setEnabled(true);
        }
    }

    private Trainer resolveTrainer(Workout workout) {
        if (workout.getTrainer() == null || workout.getTrainer().getId() == null) {
            return null;
        }

        Long trainerId = workout.getTrainer().getId();

        return trainerRepository
                .findById(trainerId)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "Trainer with id " + trainerId + " does not exist"));
    }
}
