package dev.salt.Ring20.dto.organization;

import jakarta.validation.constraints.NotBlank;

public record OrganizationApplicationRequestDto(
        @NotBlank String organizationName,
        @NotBlank String description,
        @NotBlank String city,
        @NotBlank String motivation) {}
