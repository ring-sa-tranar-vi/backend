package dev.salt.Ring20.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import dev.salt.Ring20.dto.WorkoutResponseDto;
import dev.salt.Ring20.entity.ActivityLog;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.TrainerRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ActivityLogRepository activityLogRepository;
    private final FileStorageService fileStorageService;

    public WorkoutService(
            WorkoutRepository workoutRepository,
            ActivityLogRepository activityLogRepository,
            TrainerRepository trainerRepository,
            FileStorageService fileStorageService) {
        this.workoutRepository = workoutRepository;
        this.activityLogRepository = activityLogRepository;
        this.fileStorageService = fileStorageService;
    }

    public String getWorkoutAudioUrl(Long id) {
        Workout workout =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResponseStatusException(NOT_FOUND, "Workout not found"));

        if (workout.getWorkoutAudio() == null || workout.getWorkoutAudio().isBlank()) {
            throw new ResponseStatusException(NOT_FOUND, "Workout audio not found");
        }

        return workout.getWorkoutAudio();
    }

    @Transactional
    public List<WorkoutResponseDto> getAllWorkouts(boolean includeDisabled) {
        List<Workout> workouts =
                includeDisabled
                        ? workoutRepository.findAll()
                        : workoutRepository.findByEnabledTrue();

        return workouts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public WorkoutResponseDto getWorkoutById(Long id, boolean includeDisabled) {
        Workout workout =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResponseStatusException(NOT_FOUND, "Workout not found"));

        if (!includeDisabled && Boolean.FALSE.equals(workout.getEnabled())) {
            throw new ResponseStatusException(NOT_FOUND, "Workout not found");
        }

        return mapToResponse(workout);
    }

    @Transactional
    public WorkoutResponseDto startWorkout(Long id, Long userId) {
        // Reuse the logic from getWorkoutById to ensure mapping and session handling
        WorkoutResponseDto workout = getWorkoutById(id, false);

        if (userId != null) {
            ActivityLog activityLog = new ActivityLog();
            activityLog.setUserId(userId);
            activityLog.setWorkoutId(workout.id()); // Record uses accessor style
            activityLog.setStatus("STARTED");
            activityLog.setCompletedAt(LocalDateTime.now());
            activityLogRepository.save(activityLog);
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
                                () -> new ResponseStatusException(NOT_FOUND, "Workout not found"));

        workout.setId(existing.getId());
        workout.setEnabled(existing.getEnabled());
        Workout updated = workoutRepository.save(workout);
        return mapToResponse(updated);
    }

    @Transactional
    public WorkoutResponseDto setWorkoutEnabled(Long id, boolean enabled) {
        validateId(id);

        Workout existing =
                workoutRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResponseStatusException(NOT_FOUND, "Workout not found"));

        existing.setEnabled(enabled);
        Workout updated = workoutRepository.save(existing);
        return mapToResponse(updated);
    }

    public void deleteWorkout(Long id) {
        validateId(id);

        if (!workoutRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Workout not found");
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

        String instructionsAudioUrl =
                (workout.getInstructionsAudio() != null)
                        ? fileStorageService.getFileAccess(workout.getInstructionsAudio(), 15)
                        : null;
        String workoutAudioUrl =
                (workout.getWorkoutAudio() != null)
                        ? fileStorageService.getFileAccess(workout.getWorkoutAudio(), 15)
                        : null;
        String instructionsImageUrl =
                (workout.getInstructionsImage() != null)
                        ? fileStorageService.getFileAccess(workout.getInstructionsImage(), 15)
                        : null;
        String workoutImageUrl =
                (workout.getWorkoutImage() != null)
                        ? fileStorageService.getFileAccess(workout.getWorkoutImage(), 15)
                        : null;
        String instructionsVideoUrl =
                (workout.getInstructionsVideo() != null)
                        ? fileStorageService.getFileAccess(workout.getInstructionsVideo(), 15)
                        : null;

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
                instructionsAudioUrl,
                workoutAudioUrl,
                instructionsImageUrl,
                workoutImageUrl,
                instructionsVideoUrl,
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
            throw new ResponseStatusException(BAD_REQUEST, "id must be a positive number");
        }
    }

    private void validateWorkoutForWrite(Workout workout) {
        if (workout == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Workout body is required");
        }

        if (workout.getName() == null || workout.getName().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Workout name is required");
        }

        Integer durationSeconds = workout.getDurationSeconds();
        if (durationSeconds != null && durationSeconds < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "durationSeconds cannot be negative");
        }
    }
}
