package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.repository.OrganisationRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service("organisationSecurity")
public class OrganisationSecurity {

    private final OrganisationRepository repository;
    private final SecurityService securityService;

    public OrganisationSecurity(
            OrganisationRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {
        Organization organisation =
                repository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Organisation not found with id: " + id));

        return securityService.isOwnerOrAdmin(organisation.getOrganizer().getId(), clerkId);
    }
}
