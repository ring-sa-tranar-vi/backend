package dev.salt.Ring20.dto;

import java.util.List;

public record AdminOrganisationDto(
        Long id,
        String name,
        String description,
        List<AdminOrganisationEventDto> events,
        String orgCity,
        Long organizerId) {}
