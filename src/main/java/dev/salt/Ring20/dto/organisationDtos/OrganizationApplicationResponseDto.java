package dev.salt.Ring20.dto.organisationDtos;

import dev.salt.Ring20.entity.enums.ApplicationStatus;
import dev.salt.Ring20.entity.enums.PaymentStatus;
import java.time.LocalDateTime;

public record OrganizationApplicationResponseDto(
        Long id,
        Long userId,
        String orgName,
        String description,
        String city,
        String motivation,
        ApplicationStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        PaymentStatus paymentStatus) {}
