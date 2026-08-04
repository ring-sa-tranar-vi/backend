package dev.salt.Ring20.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.salt.Ring20.config.FlexibleLocalDateTimeDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateCompanyEventDto(
        @NotBlank String name,
        String description,
        @NotNull @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
                LocalDateTime time,
        @NotBlank String city,
        String venue,
        @NotBlank String eventType,
        @Valid @NotNull OrganisationReferenceDto organisation) {}
