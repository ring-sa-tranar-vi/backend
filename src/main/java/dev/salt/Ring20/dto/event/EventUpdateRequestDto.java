package dev.salt.Ring20.dto.event;

import dev.salt.Ring20.entity.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        @NotBlank String name,
        String description,
        @NotNull LocalDateTime time,
        @NotBlank String city,
        String venue,
        @NotNull EventType eventType) {}
