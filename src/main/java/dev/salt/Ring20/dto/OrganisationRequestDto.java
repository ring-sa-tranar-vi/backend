package dev.salt.Ring20.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrganisationRequestDto(
        @NotBlank String name,
        String description,
        @Valid List<@Valid EventRequestDto> events,
        @NotBlank String orgCity,
        @NotNull Long organizerId) {}
