package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Organization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganisationRepository extends JpaRepository<Organization, Long> {
    boolean existsByOrganizer_Id(Long organizerId);

    @Query(
            "SELECT DISTINCT organisation FROM Organization organisation LEFT JOIN FETCH organisation.events")
    List<Organization> findAllWithEvents();

    @Query(
            "SELECT organisation FROM Organization organisation LEFT JOIN FETCH organisation.events WHERE organisation.id = :id")
    Optional<Organization> findByIdWithEvents(Long id);

    @Query(
            """
                SELECT o FROM Organization o
                LEFT JOIN FETCH o.events
                WHERE o.organizer.clerkId = :clerkId
            """)
    List<Organization> findByOrganizer_ClerkIdWithEvents(String clerkId);

    Optional<Organization> findFirstByOrganizer_ClerkId(String clerkId);
}
