package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.OrganisationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganisationService {
    private final OrganisationRepository repo;

    public OrganisationService(OrganisationRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Organisation createOrganisation(String name, String description, List<Event> events) {
        Organisation organisation = new Organisation(name, description);
        attachOrganisationToEvents(organisation, events);
        return repo.save(organisation);
    }

    @Transactional(readOnly = true)
    public List<Organisation> getAllOrganisations() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Organisation getOrganisationById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation not found with id: " + id));
    }

    public void deleteOrganisationById(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public Organisation updateOrganisationById(
            Long id, String name, String description, List<Event> events) {
        Organisation foundOrg =
                repo.findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Organisation not found with id: " + id));
        foundOrg.setName(name);
        foundOrg.setDescription(description);
        foundOrg.setEvents(events);
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
