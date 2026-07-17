package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventResponseDto(long id, String name, String description, LocalDateTime time, Long organisationId,
                               String city, String venue, EventType eventType) {
}
