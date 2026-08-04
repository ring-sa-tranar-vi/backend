package dev.salt.Ring20.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.salt.Ring20.config.FlexibleLocalDateTimeDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AdminCreateEventDto(
        @NotNull Long organisationId,
        @NotBlank String name,
        String description,
        @NotNull @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
                LocalDateTime time) {}
