package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.OrganisationRepository;
import org.springframework.stereotype.Service;
//TODO: not consistent as with the SecurityService
@Service
public class OrganisationSecurity {

    private final OrganisationRepository repository;
    private final SecurityService securityService;

    public OrganisationSecurity(
            OrganisationRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {
        Organisation organisation = repository.findById(id).orElseThrow();

        return securityService.isOwnerOrAdmin(organisation.getOrganizer().getId(), clerkId);
    }
}
