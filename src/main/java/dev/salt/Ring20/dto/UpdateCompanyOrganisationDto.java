package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCompanyOrganisationDto(
        @NotBlank String name,
        String description,
        @NotBlank String orgCity) {
}
