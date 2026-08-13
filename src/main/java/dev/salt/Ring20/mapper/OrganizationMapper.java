package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.admin.AdminOrganizationDto;
import dev.salt.Ring20.dto.admin.AdminOrganizationEventDto;
import dev.salt.Ring20.dto.company.CompanyOrganizationDto;
import dev.salt.Ring20.dto.company.UpdateCompanyOrganizationDto;
import dev.salt.Ring20.dto.event.EventResponseDto;
import dev.salt.Ring20.dto.organization.OrganizationCreateRequestDto;
import dev.salt.Ring20.dto.organization.OrganizationResponseDto;
import dev.salt.Ring20.dto.organization.OrganizationUpdateRequestDto;
import dev.salt.Ring20.entity.Organization;

import java.util.List;

public class OrganizationMapper {
    public static CompanyOrganizationDto toCompanyOrganisationDto(Organization organisation) {
        return new CompanyOrganizationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }

    public static OrganizationResponseDto toResponseDto(Organization organisation) {
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
                organisation.getMotivation(),
                organisation.getUsersFollowing());
    }

    public static Organization toOrganization(OrganizationUpdateRequestDto dto) {
        Organization organization = new Organization();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        return organization;
    }

    public static Organization toOrganization(UpdateCompanyOrganizationDto dto) {
        Organization organization = new Organization();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        return organization;
    }

    public static Organization toOrganization(OrganizationCreateRequestDto dto) {
        Organization organization = new Organization();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        organization.setMotivation(dto.motivation());
        return organization;
    }

    public static AdminOrganizationDto toOrganisationDto(Organization organisation) {
        List<AdminOrganizationEventDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream().map(EventMapper::toEventDto).toList();
        return new AdminOrganizationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events,
                organisation.getOrgCity(),
                organisation.getOrganizer() == null ? null : organisation.getOrganizer().getId());
    }
}
