package dev.salt.Ring20.dto.event;

import dev.salt.Ring20.entity.enums.EventType;
import java.time.LocalDateTime;

public record EventResponseDto(
        long id,
        String name,
        String description,
        LocalDateTime time,
        Long organisationId,
        String city,
        String venue,
        EventType eventType) {}
