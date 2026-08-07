package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.trainerDtos.TrainerRequestDto;
import dev.salt.Ring20.dto.workoutDtos.RecommendWorkoutResponseDto;
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

}
