package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record EventResponseDto(
        long id,
        String name,
        String description,
        LocalDateTime time,
        Long organisationId) {}
