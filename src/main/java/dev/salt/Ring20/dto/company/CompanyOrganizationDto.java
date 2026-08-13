package dev.salt.Ring20.dto.company;

public record CompanyOrganizationDto(
        Long id, String name, String description, String orgCity, int followerCount) {}
