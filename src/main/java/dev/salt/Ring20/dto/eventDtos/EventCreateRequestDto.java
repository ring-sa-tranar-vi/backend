package dev.salt.Ring20.dto.eventDtos;

import dev.salt.Ring20.entity.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank String name,
        String description,
        @NotNull LocalDateTime time,
        @NotNull Long organisationId,
        @NotBlank String city,
        String venue,
        @NotNull EventType eventType) {}
