package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AdminCreateEventDto(
        @NotNull Long organisationId,
        @NotBlank String name,
        String description,
        @NotNull LocalDateTime time) {}
