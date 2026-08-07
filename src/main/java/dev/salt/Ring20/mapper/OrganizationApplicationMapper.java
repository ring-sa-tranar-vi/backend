package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.organisationDtos.OrganizationApplicationRequestDto;
import dev.salt.Ring20.dto.organisationDtos.OrganizationApplicationResponseDto;
import dev.salt.Ring20.entity.OrganizationApplication;

public class OrganizationApplicationMapper {
    public static OrganizationApplicationResponseDto toResponse(
            OrganizationApplication application) {
        return new OrganizationApplicationResponseDto(
                application.getId(),
                application.getUser().getId(),
                application.getOrganizationName(),
                application.getDescription(),
                application.getCity(),
                application.getMotivation(),
                application.getApplicationStatus(),
                application.getCreatedAt(),
                application.getReviewedAt(),
                application.getPaymentStatus());
    }

    public static OrganizationApplication toEntity(OrganizationApplicationRequestDto dto) {
        OrganizationApplication application = new OrganizationApplication();
        application.setOrganizationName(dto.organizationName());
        application.setDescription(dto.description());
        application.setCity(dto.city());
        application.setMotivation(dto.motivation());
        return application;
    }
}
