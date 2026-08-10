package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.OrganizationRepository;
import dev.salt.Ring20.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository repo, UserRepository userRepo) {
        this.organizationRepository = repo;
        this.userRepository = userRepo;
    }

    @Transactional(readOnly = true)
    public boolean hasOrganisation(Long userId) {
        return organizationRepository.existsByOrganizer_Id(userId);
    }

    @Transactional
    public Organization createOrganisation(Organization organization, Long userId) {
        User organizer = getUserById(userId);
        if (hasOrganisation(userId)) {
            throw new IllegalStateException("User already organizes an organisation");
        }

        organization.setOrganizer(organizer);
        return organizationRepository.save(organization);
    }

    @Transactional(readOnly = true)
    public List<Organization> getAllOrganisations() {
        return organizationRepository.findAllWithEvents();
    }

    @Transactional(readOnly = true)
    public Organization getOrganisationById(Long id) {
        return organizationRepository
                .findByIdWithEvents(id)
                .orElseThrow(
                        () -> new NoSuchElementException("Organisation not found with id: " + id));
    }

    @Transactional
    public void deleteOrganisationById(Long id) {
        organizationRepository.delete(getOrganisationById(id));
    }

    //    @Transactional
    //    public Organisation updateOrganisationById(
    //            Long id, String name, String description, String orgCity) {
    //        return updateOrganisationById(id, name, description, orgCity, null);
    //    }

    @Transactional
    public Organization updateOrganisationById(Organization organization, Long id) {

        Organization foundOrg = getOrganisationById(id);

        foundOrg.setName(organization.getName());
        foundOrg.setDescription(organization.getDescription());
        foundOrg.setOrgCity(organization.getOrgCity());

        return organizationRepository.save(foundOrg);
    }

    public List<Organization> getOrganisationForUser(String clerkId) {
        List<Organization> organisations =
                organizationRepository.findByOrganizer_ClerkIdWithEvents(clerkId);

        if (organisations.isEmpty()) {
            throw new NoSuchElementException(
                    "No organisations found for user with clerkId: " + clerkId);
        }

        return organisations;
    }

    @Transactional(readOnly = true)
    public Optional<Organization> findOrganisationForUser(String clerkId) {
        return organizationRepository.findFirstByOrganizer_ClerkId(clerkId);
    }

    private User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("No user found with this id:  " + userId));
    }
}
