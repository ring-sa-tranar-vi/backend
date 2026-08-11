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
    public boolean hasOrganization(Long userId) {
        return organizationRepository.existsByOrganizer_Id(userId);
    }

    @Transactional
    public Organization createOrganization(Organization organization, Long userId) {
        User organizer = getUserById(userId);
        if (hasOrganization(userId)) {
            throw new IllegalStateException("User already organizes an organization");
        }

        organization.setOrganizer(organizer);
        return organizationRepository.save(organization);
    }

    @Transactional(readOnly = true)
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAllWithEvents();
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationById(Long id) {
        return organizationRepository
                .findByIdWithEvents(id)
                .orElseThrow(
                        () -> new NoSuchElementException("organization not found with id: " + id));
    }

    @Transactional
    public void deleteOrganizationById(Long id) {
        organizationRepository.delete(getOrganizationById(id));
    }

    //    @Transactional
    //    public organization updateorganizationById(
    //            Long id, String name, String description, String orgCity) {
    //        return updateorganizationById(id, name, description, orgCity, null);
    //    }

    @Transactional
    public Organization updateOrganizationById(Organization organization, Long id) {

        Organization foundOrg = getOrganizationById(id);

        foundOrg.setName(organization.getName());
        foundOrg.setDescription(organization.getDescription());
        foundOrg.setOrgCity(organization.getOrgCity());

        return organizationRepository.save(foundOrg);
    }

    public List<Organization> getOrganizationForUser(String clerkId) {
        List<Organization> organizations =
                organizationRepository.findByOrganizer_ClerkIdWithEvents(clerkId);

        if (organizations.isEmpty()) {
            throw new NoSuchElementException(
                    "No organizations found for user with clerkId: " + clerkId);
        }

        return organizations;
    }

    @Transactional(readOnly = true)
    public Optional<Organization> findOrganizationForUser(String clerkId) {
        return organizationRepository.findFirstByOrganizer_ClerkId(clerkId);
    }

    private User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("No user found with this id:  " + userId));
    }
}
