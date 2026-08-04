package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.userDtos.UserRequestDto;
import dev.salt.Ring20.entity.User;

public class UserMapper {
    public static User toUserEntity(UserRequestDto request) {
        User user = new User();

        user.setName(request.name());
        user.setIntensityLevel(request.intensityLevel());
        user.setContext(request.context());
        user.setTrainerId(request.trainerId());
        user.setCity(request.city());
        user.setOnboarding(request.onboarding());

        return user;
    }
}
