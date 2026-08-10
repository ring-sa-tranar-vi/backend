package dev.salt.Ring20.service.security;

import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.OrganizationRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service("organisationSecurity")
public class OrganizationSecurity {

    private final OrganizationRepository repository;
    private final SecurityService securityService;

    public OrganizationSecurity(
            OrganizationRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public boolean canModify(Long id, String clerkId) {
        Organisation organisation =
                repository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Organisation not found with id: " + id));

        return securityService.isOwnerOrAdmin(organisation.getOrganizer().getId(), clerkId);
    }
}
