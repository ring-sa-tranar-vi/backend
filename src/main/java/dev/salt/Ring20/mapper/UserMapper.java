package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.user.UserRequestDto;
import dev.salt.Ring20.dto.user.UserResponseDto;
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

    public static UserResponseDto toResponse(User user, boolean isAdmin) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getIntensityLevel(),
                user.getContext(),
                isAdmin,
                user.getTrainerId(),
                user.getCity(),
                user.isOnboarding());
    }

    public static UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getIntensityLevel(),
                user.getContext(),
                "ADMIN".equals(user.getRole()),
                user.getTrainerId(),
                user.getCity(),
                user.isOnboarding());
    }
}
