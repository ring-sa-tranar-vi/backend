package dev.salt.Ring20.dto.organization;

import dev.salt.Ring20.dto.event.EventResponseDto;
import java.util.List;

public record OrganisationResponseDto(
        Long id,
        String name,
        String description,
        List<EventResponseDto> events,
        String orgCity,
        Long organizerId,
        String motivation) {}
