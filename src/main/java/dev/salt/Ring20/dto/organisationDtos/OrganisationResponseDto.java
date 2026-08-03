package dev.salt.Ring20.dto.organisationDtos;

import dev.salt.Ring20.dto.eventDtos.EventResponseDto;
import java.util.List;

public record OrganisationResponseDto(
        Long id,
        String name,
        String description,
        List<EventResponseDto> events,
        String orgCity,
        Long organizerId) {}
