package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT event FROM Event event JOIN FETCH event.organisation")
    List<Event> findAllWithOrganisation();

    List<Event> findByOrganisationId(Long organisationId);

    void deleteByOrganisationId(Long organisationId);
}
