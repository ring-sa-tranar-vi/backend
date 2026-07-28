package dev.salt.Ring20.dto;

import jakarta.validation.constraints.NotBlank;

public class OrganizationApplicationRequestDto {
    @NotBlank
    private String organizationName;
    @NotBlank
    private String description;
    @NotBlank
    private String motivation;
}
