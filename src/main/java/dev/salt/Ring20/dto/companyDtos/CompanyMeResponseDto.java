package dev.salt.Ring20.dto.companyDtos;

public record CompanyMeResponseDto(
        Long userId,
        String role,
        boolean canManageOrganisation,
        Long organisationId,
        String organisationName) {}
