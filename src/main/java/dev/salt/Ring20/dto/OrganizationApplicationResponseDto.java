package dev.salt.Ring20.dto;

import dev.salt.Ring20.entity.ApplicationStatus;
import dev.salt.Ring20.entity.PaymentStatus;
import java.time.LocalDateTime;

public record OrganizationApplicationResponseDto(
        Long id,
        Long userId,
        String orgName,
        String description,
        String motivation,
        ApplicationStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        PaymentStatus paymentStatus) {}
