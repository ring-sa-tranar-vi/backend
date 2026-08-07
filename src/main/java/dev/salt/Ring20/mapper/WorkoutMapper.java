package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.workoutDtos.WorkoutRequestDto;
import dev.salt.Ring20.dto.workoutDtos.WorkoutResponseDto;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.service.storage.FileStorageService;

public class WorkoutMapper {

    public static WorkoutResponseDto toWorkoutResponse(
            Workout workout, FileStorageService fileStorageService, int validMinutes) {
        String imageUrl = getFileUrl(workout.getImage(), fileStorageService, validMinutes);

        String videoUrl = getFileUrl(workout.getVideo(), fileStorageService, validMinutes);

        return new WorkoutResponseDto(
                workout.getId(),
                workout.getName(),
                workout.getDescription(),
                workout.getDashboardName(),
                workout.getDashboardDescription(),
                workout.getInstructions(),
                workout.getGuidance(),
                workout.getLevel(),
                workout.getType(),
                imageUrl,
                videoUrl,
                workout.getEnabled());
    }

    public static Workout toEntity(WorkoutRequestDto request) {
        Workout workout = new Workout();
        workout.setName(request.name());
        workout.setDescription(request.description());
        workout.setDashboardName(request.dashboardName());
        workout.setDashboardDescription(request.dashboardDescription());
        workout.setInstructions(request.instructions());
        workout.setGuidance(request.guidance());
        workout.setLevel(request.level());
        workout.setType(request.type());
        workout.setImage(request.image());
        workout.setVideo(request.video());

        return workout;
    }

    private static String getFileUrl(
            String file, FileStorageService fileStorageService, int validMinutes) {

        return file != null ? fileStorageService.getFileAccess(file, validMinutes) : null;
    }
}
