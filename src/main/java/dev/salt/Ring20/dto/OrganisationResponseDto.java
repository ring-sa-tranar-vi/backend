package dev.salt.Ring20.dto;

import java.util.List;

public record OrganisationResponseDto(
        Long id, String name, String description, List<EventResponseDto> events) {}
