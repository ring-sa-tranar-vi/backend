package dev.salt.Ring20.dto.organisation;

import jakarta.validation.constraints.NotBlank;

public record OrganizationApplicationRequestDto(
        @NotBlank String organizationName,
        @NotBlank String description,
        @NotBlank String city,
        @NotBlank String motivation) {}
