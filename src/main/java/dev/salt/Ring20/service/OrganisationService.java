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
    //TODO: consistent naming -> other controllers have names like eventRepository
    private final OrganisationRepository repo;
    private final UserRepository userRepo;

    public OrganisationService(OrganisationRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }
    // TODO: formating line indentation
    @Transactional
    public Organisation createOrganisation(
            String name, String description, String orgCity, Long userId) {
        User organizer = getUserById(userId);

        Organisation organisation = new Organisation(name, description, orgCity, organizer);
        return repo.save(organisation);
    }

    @Transactional(readOnly = true)
    public List<Organisation> getAllOrganisations() {
        return repo.findAllWithEvents();
    }

    // TODO: formating line indentation

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

    // TODO: formating line indentation
    @Transactional
    public Organisation updateOrganisationById(
            Long id, String name, String description, String orgCity) {
        Organisation foundOrg = getOrganisationById(id);
        foundOrg.setName(name);
        foundOrg.setDescription(description);
        foundOrg.setOrgCity(orgCity);
        ;
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

    private User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("No user found with this id:  " + userId));
    }
}