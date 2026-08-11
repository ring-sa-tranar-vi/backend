package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.enums.CallBackStatus;
import java.time.Instant;

public record ScheduledCallDto(
        Long id, Long userId, Long trainerId, Instant targetTime, CallBackStatus callBackStatus) {}
