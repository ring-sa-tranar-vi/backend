package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.Organisation;

import java.time.LocalDateTime;

public record EventRequestDto(
        String name,
        String description,
        LocalDateTime time,
        Organisation organisation
) {
}
