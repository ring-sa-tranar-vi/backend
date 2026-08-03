package dev.salt.Ring20.dto.organisationDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganisationCreateRequestDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String orgCity,
        @NotNull Long organizerId) {}
