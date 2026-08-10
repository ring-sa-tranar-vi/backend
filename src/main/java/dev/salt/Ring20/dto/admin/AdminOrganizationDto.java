package dev.salt.Ring20.dto.admin;

import java.util.List;

public record AdminOrganizationDto(
        Long id,
        String name,
        String description,
        List<AdminOrganizationEventDto> events,
        String orgCity,
        Long organizerId) {}
