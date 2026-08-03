package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;

public record CompanyOrganisationRequestDto(
        @NotBlank String name, String description, @NotBlank String orgCity) {}
