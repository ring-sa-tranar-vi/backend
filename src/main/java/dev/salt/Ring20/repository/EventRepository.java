package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganisationId(Long organisationId);
}
