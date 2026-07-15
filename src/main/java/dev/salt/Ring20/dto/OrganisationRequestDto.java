package dev.salt.Ring20.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record OrganisationRequestDto(
	@NotBlank String name,
	String description,
	List<@Valid EventRequestDto> events) {}
