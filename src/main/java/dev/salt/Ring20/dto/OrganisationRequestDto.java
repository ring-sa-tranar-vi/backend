package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.Event;
import java.util.List;

public record OrganisationRequestDto(String name, String description, List<Event> events) {}
