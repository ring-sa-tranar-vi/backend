package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record AdminEventResponseDTO(
        Long id,
        String name,
        String description,
        LocalDateTime time,
        Long organisationId,
        String organisationName) {}

