package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.ScheduledCallDto;
import dev.salt.Ring20.entity.ScheduledCall;

public class ScheduledCallMapper {
    public static ScheduledCallDto toScheduledCallDto(ScheduledCall call) {
        return new ScheduledCallDto(
                call.getId(),
                call.getUserId(),
                call.getTrainerId(),
                call.getTargetTime(),
                call.getCallBackStatus());
    }
}
