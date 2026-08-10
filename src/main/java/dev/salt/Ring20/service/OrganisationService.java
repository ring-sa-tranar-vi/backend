package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganisationService {
    private final OrganisationRepository repo;
    private final UserRepository userRepo;

    public OrganisationService(OrganisationRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public boolean hasOrganisation(Long userId) {
        return repo.existsByOrganizer_Id(userId);
    }

    @Transactional
    public Organisation createOrganisation(
            String name, String description, String orgCity, Long userId, String motivation) {
        User organizer = getUserById(userId);

        if (hasOrganisation(userId)) {
            throw new IllegalStateException("User already organizes an organisation");
        }

        Organisation organisation =
                new Organisation(name, description, orgCity, organizer, motivation);
        return repo.save(organisation);
    }

    @Transactional(readOnly = true)
    public List<Organisation> getAllOrganisations() {
        return repo.findAllWithEvents();
    }

    @Transactional(readOnly = true)
    public Organisation getOrganisationById(Long id) {
        return repo.findByIdWithEvents(id)
                .orElseThrow(
                        () -> new NoSuchElementException("Organisation not found with id: " + id));
    }

    @Transactional
    public void deleteOrganisationById(Long id) {
        repo.delete(getOrganisationById(id));
    }

    @Transactional
    public Organisation updateOrganisationById(
            Long id, String name, String description, String orgCity) {
        return updateOrganisationById(id, name, description, orgCity, null);
    }

    @Transactional
    public Organisation updateOrganisationById(
            Long id, String name, String description, String orgCity, Long organizerId) {
        Organisation foundOrg = getOrganisationById(id);
        foundOrg.setName(name);
        foundOrg.setDescription(description);
        foundOrg.setOrgCity(orgCity);
        if (organizerId != null) {
            foundOrg.setOrganizer(getUserById(organizerId));
        }
        return repo.save(foundOrg);
    }

    public List<Organisation> getOrganisationForUser(String clerkId) {
        List<Organisation> organisations = repo.findByOrganizer_ClerkIdWithEvents(clerkId);

        if (organisations.isEmpty()) {
            throw new NoSuchElementException(
                    "No organisations found for user with clerkId: " + clerkId);
        }

        return organisations;
    }

    @Transactional(readOnly = true)
    public Optional<Organisation> findOrganisationForUser(String clerkId) {
        return repo.findFirstByOrganizer_ClerkId(clerkId);
    }

    private User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("No user found with this id:  " + userId));
    }
}
