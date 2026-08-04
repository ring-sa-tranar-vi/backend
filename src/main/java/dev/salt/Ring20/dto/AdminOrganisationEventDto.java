package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record AdminOrganisationEventDto(
        Long id, String name, String description, LocalDateTime time, Long organisationId) {}
