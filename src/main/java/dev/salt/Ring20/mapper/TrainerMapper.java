package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.trainerDtos.TrainerRequestDto;
import dev.salt.Ring20.dto.trainerDtos.TrainerResponseDto;
import dev.salt.Ring20.dto.workoutDtos.RecommendWorkoutResponseDto;
import dev.salt.Ring20.entity.Trainer;
import dev.salt.Ring20.service.FileStorageService;
import dev.salt.Ring20.service.data.RecommendedWorkoutData;
import dev.salt.Ring20.service.data.TrainerData;

public class TrainerMapper {
    public static TrainerData toTrainerData(TrainerRequestDto request) {
        return new TrainerData(
                request.name(),
                request.prompt(),
                request.voice(),
                request.intro(),
                request.language(),
                request.imageSelect(),
                request.imageCall(),
                request.imageStart(),
                request.ambience());
    }

    public static RecommendWorkoutResponseDto toRecommendedWorkoutResponse(RecommendedWorkoutData data) {
        return new RecommendWorkoutResponseDto(data.workoutId(), data.reasoning());
    }

    public static TrainerResponseDto toResponseDto(Trainer trainer, FileStorageService fileStorageService, int validMinutes) {
        String introUrl = getFileUrl(trainer.getIntro(), fileStorageService, validMinutes);

        String imageSelectUrl = getFileUrl(trainer.getImageSelect(), fileStorageService, validMinutes);

        String imageCallUrl = getFileUrl(trainer.getImageCall(), fileStorageService, validMinutes);

        String imageStartUrl = getFileUrl(trainer.getImageStart(), fileStorageService, validMinutes);

        return new TrainerResponseDto(
                trainer.getId(),
                trainer.getName(),
                trainer.getPrompt(),
                trainer.getVoice(),
                introUrl,
                trainer.getLanguage(),
                imageSelectUrl,
                imageCallUrl,
                imageStartUrl,
                trainer.getAmbience());
    }

    private static String getFileUrl(
            String file,
            FileStorageService fileStorageService,
            int validMinutes) {

        return file != null
                ? fileStorageService.getFileAccess(file, validMinutes)
                : null;
    }

}
