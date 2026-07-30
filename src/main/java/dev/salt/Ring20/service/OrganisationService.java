package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
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
            String name, String description, List<Event> events, String orgCity, Long userId) {
        User organizer =
                userRepo.findById(userId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No user foudn with this id:  " + userId));
        Organisation organisation = new Organisation(name, description, events, orgCity, organizer);
        attachOrganisationToEvents(organisation, events);
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
        repo.deleteById(id);
    }

    // TODO: formating line indentation
    @Transactional
    public Organisation updateOrganisationById(
            Long id, String name, String description, List<Event> events, String orgCity) {
        Organisation foundOrg =
                repo.findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Organisation not found with id: " + id));
        foundOrg.setName(name);
        foundOrg.setDescription(description);
        foundOrg.setEvents(events);
        foundOrg.setOrgCity(orgCity);
        attachOrganisationToEvents(foundOrg, events);
        return repo.save(foundOrg);
    }

    private void attachOrganisationToEvents(Organisation organisation, List<Event> events) {
        if (events == null) {
            return;
        }

        for (Event event : events) {
            event.setOrganisation(organisation);
        }
    }
}