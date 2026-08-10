package dev.salt.Ring20.dto.company;

import jakarta.validation.constraints.NotBlank;

public record UpdateCompanyOrganizationDto(
        @NotBlank String name, String description, @NotBlank String orgCity) {}
