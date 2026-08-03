package dev.salt.Ring20.dto.organisationDtos;

import jakarta.validation.constraints.NotBlank;

public record OrganisationUpdateRequestDto(
        @NotBlank String name, @NotBlank String description, @NotBlank String orgCity) {}
