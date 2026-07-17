package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record AdminEventRequestDTO(
        String name, String description, LocalDateTime time, Long organisationId) {}
