package dev.salt.Ring20.dto;

public record CompanyMeDto(
        Long userId,
        String role,
        boolean canManageOrganisation,
        Long organisationId,
        String organisationName) {}
