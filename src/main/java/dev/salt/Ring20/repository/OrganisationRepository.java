package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Organisation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    boolean existsByOrganizer_Id(Long organizerId);

    @Query(
            "SELECT DISTINCT organisation FROM Organisation organisation LEFT JOIN FETCH organisation.events")
    List<Organisation> findAllWithEvents();

    @Query(
            "SELECT organisation FROM Organisation organisation LEFT JOIN FETCH organisation.events WHERE organisation.id = :id")
    Optional<Organisation> findByIdWithEvents(Long id);

    @Query(
            """
                SELECT o FROM Organisation o
                LEFT JOIN FETCH o.events
                WHERE o.organizer.clerkId = :clerkId
            """)
    List<Organisation> findByOrganizer_ClerkIdWithEvents(String clerkId);

    Optional<Organisation> findFirstByOrganizer_ClerkId(String clerkId);
}
