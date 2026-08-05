package dev.salt.Ring20.mapper;

import dev.salt.Ring20.dto.company.CompanyOrganisationDto;
import dev.salt.Ring20.entity.Organisation;

public class OrganizationMapper {
    public static CompanyOrganisationDto toCompanyOrganisationDto(Organisation organisation){
        return new CompanyOrganisationDto(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getOrgCity());
    }
}
