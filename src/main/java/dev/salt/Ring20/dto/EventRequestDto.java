package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.EventType;
import dev.salt.Ring20.entity.Organisation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventRequestDto(
        @NotBlank String name,
        String description,
        @NotNull LocalDateTime time,
        @NotNull Organisation organisation,
        @NotBlank String city,
        String venue,
        @NotNull EventType eventType) {}
