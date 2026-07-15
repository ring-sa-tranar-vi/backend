package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganisationId(Long organisationId);
}
