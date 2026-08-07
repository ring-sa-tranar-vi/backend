package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.workoutDtos.WorkoutResponseDto;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.service.storage.FileStorageService;

import javax.swing.plaf.PanelUI;

public class WorkoutMapper {

    public static WorkoutResponseDto toWorkoutResponse(Workout workout, FileStorageService fileStorageService,  int validMinutes) {
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


    private static String getFileUrl(
            String file, FileStorageService fileStorageService, int validMinutes) {

        return file != null ? fileStorageService.getFileAccess(file, validMinutes) : null;
    }
}
