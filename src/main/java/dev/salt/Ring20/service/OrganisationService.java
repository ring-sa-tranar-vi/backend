package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganisationService {

    private final OrganisationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganisationService(OrganisationRepository repo, UserRepository userRepo) {
        this.organizationRepository = repo;
        this.userRepository = userRepo;
    }

    @Transactional
    public Organisation createOrganisation(Organisation organization, Long userId) {
        User organizer = getUserById(userId);

        organization.setOrganizer(organizer);
        return organizationRepository.save(organization);
    }

    @Transactional(readOnly = true)
    public List<Organisation> getAllOrganisations() {
        return organizationRepository.findAllWithEvents();
    }

    @Transactional(readOnly = true)
    public Organisation getOrganisationById(Long id) {
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
    public Organisation updateOrganisationById(Organisation organization, Long id) {

        Organisation foundOrg = getOrganisationById(id);

        foundOrg.setName(organization.getName());
        foundOrg.setDescription(organization.getDescription());
        foundOrg.setOrgCity(organization.getOrgCity());

        return organizationRepository.save(foundOrg);
    }

    public List<Organisation> getOrganisationForUser(String clerkId) {
        List<Organisation> organisations =
                organizationRepository.findByOrganizer_ClerkIdWithEvents(clerkId);

        if (organisations.isEmpty()) {
            throw new NoSuchElementException(
                    "No organisations found for user with clerkId: " + clerkId);
        }

        return organisations;
    }

    private User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("No user found with this id:  " + userId));
    }
}
