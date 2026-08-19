package dev.salt.Ring20.dto.organization;

import jakarta.validation.constraints.NotBlank;

public record OrganizationUpdateRequestDto(
        @NotBlank String name, @NotBlank String description, @NotBlank String orgCity) {}
