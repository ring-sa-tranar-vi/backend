package dev.salt.Ring20.dto.company;

import java.time.LocalDateTime;

public record CompanyEventDto(
        long id,
        String name,
        String description,
        LocalDateTime time,
        String city,
        String venue,
        int attendeesCount,
        String eventType) {}
