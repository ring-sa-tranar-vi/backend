package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganisationService {
    private OrganisationRepository repo;

    public OrganisationService(OrganisationRepository repo) {
        this.repo = repo;
    }

    public Organisation createOrganisation(String name, String description, List<Event> events){
        Organisation newOrg = new Organisation(name, description, events);
        return repo.save(newOrg);
    }
}
