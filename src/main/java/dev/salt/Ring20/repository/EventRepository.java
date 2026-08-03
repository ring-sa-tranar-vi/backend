package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT event FROM Event event JOIN FETCH event.organisation")
    List<Event> findAllWithOrganisation();

    List<Event> findByOrganisationId(Long organisationId);

    @Query(
            """
       SELECT e
       FROM Event e
       LEFT JOIN FETCH e.organisation
       WHERE e.id = :id
       """)
    Optional<Event> findByIdWithOrganisation(@Param("id") Long id);
}
