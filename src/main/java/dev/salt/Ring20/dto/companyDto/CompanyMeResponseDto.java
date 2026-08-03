package dev.salt.Ring20.dto.companyDto;

public record CompanyMeResponseDto(
        Long userId,
        String role,
        boolean canManageOrganisation,
        Long organisationId,
        String organisationName) {}
