package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganisationCreateRequestDto(
        @NotBlank String name,
        String description,
        @NotBlank String orgCity,
        @NotNull Long organizerId) {}
