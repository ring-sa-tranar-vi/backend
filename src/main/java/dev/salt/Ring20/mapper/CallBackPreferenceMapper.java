package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.callbackDtos.CallbackPreferenceRequestDto;
import dev.salt.Ring20.dto.callbackDtos.CallbackPreferenceResponseDto;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.enums.DayOfWeekType;
import dev.salt.Ring20.entity.enums.RepeatType;

public class CallBackPreferenceMapper {
    public static CallbackPreference toCallbackPreference(CallbackPreferenceRequestDto request) {
        CallbackPreference callback = new CallbackPreference();
        callback.setDay(DayOfWeekType.valueOf(request.day()));
        callback.setTime(request.time());
        callback.setRepeat(RepeatType.valueOf(request.repeatType()));
        return callback;
    }

    public static CallbackPreferenceResponseDto toCallbackResponse(CallbackPreference callback) {

        return new CallbackPreferenceResponseDto(
                callback.getId(),
                callback.getDay().name(),
                callback.getTime(),
                callback.getRepeat().name());
    }
}
