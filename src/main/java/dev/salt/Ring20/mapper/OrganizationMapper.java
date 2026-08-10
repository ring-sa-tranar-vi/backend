package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.company.CompanyOrganizationDto;
import dev.salt.Ring20.dto.company.UpdateCompanyOrganizationDto;
import dev.salt.Ring20.dto.event.EventResponseDto;
import dev.salt.Ring20.dto.organization.OrganizationCreateRequestDto;
import dev.salt.Ring20.dto.organization.OrganizationResponseDto;
import dev.salt.Ring20.dto.organization.OrganizationUpdateRequestDto;
import dev.salt.Ring20.entity.Organisation;
import java.util.List;

public class OrganizationMapper {
    public static CompanyOrganizationDto toCompanyOrganisationDto(Organisation organisation) {
        return new CompanyOrganizationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }

    public static OrganizationResponseDto toResponseDto(Organisation organisation) {
        List<EventResponseDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream()
                                .map(EventMapper::toEventResponseDto)
                                .toList();
        return new OrganizationResponseDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events,
                organisation.getOrgCity(),
                organisation.getOrganizer() != null ? organisation.getOrganizer().getId() : null,
                organisation.getMotivation());
    }

    public static Organisation toOrganization(OrganizationUpdateRequestDto dto) {
        Organisation organization = new Organisation();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        return organization;
    }

    public static Organisation toOrganization(UpdateCompanyOrganizationDto dto) {
        Organisation organization = new Organisation();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        return organization;
    }

    public static Organisation toOrganization(OrganizationCreateRequestDto dto) {
        Organisation organization = new Organisation();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        organization.setMotivation(dto.motivation());
        return organization;
    }
}
