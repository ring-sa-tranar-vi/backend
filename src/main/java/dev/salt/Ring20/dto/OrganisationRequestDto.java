package dev.salt.Ring20.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record OrganisationRequestDto(
        @NotBlank String name, String description, List<@Valid EventRequestDto> events) {}
