package dev.salt.Ring20.dto;

import java.time.LocalDateTime;

public record CompanyEventResponseDto(
        long id,
        String name,
        String description,
        LocalDateTime time,
        String city,
        String venue,
        int attendeesCount) {}
