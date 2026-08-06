package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.UpdateCompanyOrganisationDto;
import dev.salt.Ring20.dto.company.CompanyOrganisationDto;
import dev.salt.Ring20.dto.eventDtos.EventResponseDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationCreateRequestDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationResponseDto;
import dev.salt.Ring20.dto.organisationDtos.OrganisationUpdateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;

import java.util.List;

public class OrganizationMapper {
    public static CompanyOrganisationDto toCompanyOrganisationDto(Organisation organisation){
        return new CompanyOrganisationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }
    public static OrganisationResponseDto toResponseDto(Organisation organisation) {
        List<EventResponseDto> events =
                organisation.getEvents() == null
                        ? List.of()
                        : organisation.getEvents().stream().map(EventMapper::toEventResponseDto).toList();
        return new OrganisationResponseDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                events,
                organisation.getOrgCity(),
                organisation.getOrganizer() != null ? organisation.getOrganizer().getId() : null,
                organisation.getMotivation());
    }

    public static Organisation toOrganization(OrganisationUpdateRequestDto dto){
        Organisation organization = new Organisation();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        return organization;
    }

    public static Organisation toOrganization(UpdateCompanyOrganisationDto dto){
        Organisation organization = new Organisation();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        return organization;
    }

    public static Organisation toOrganization(OrganisationCreateRequestDto dto){
        Organisation organization = new Organisation();
        organization.setName(dto.name());
        organization.setDescription(dto.description());
        organization.setOrgCity(dto.orgCity());
        organization.setMotivation(dto.motivation());
        return organization;
    }

}
