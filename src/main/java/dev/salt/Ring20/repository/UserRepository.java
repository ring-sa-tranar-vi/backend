package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClerkId(String clerkId);

    @Query(
            """
                SELECT u FROM User u
                LEFT JOIN FETCH u.attendingEvents
                WHERE u.id = :id
            """)
    Optional<User> findByIdWithAttendingEvents(@Param("id") Long id);

    @Query(
            """
                SELECT u FROM User u
                LEFT JOIN FETCH u.followedOrganisations
                WHERE u.id = :id
            """)
    Optional<User> findByIdWithFollowedOrganisations(@Param("id") Long id);

    @Query(
            """
                SELECT DISTINCT organisation FROM User u
                JOIN u.followedOrganisations organisation
                LEFT JOIN FETCH organisation.events
                WHERE u.id = :id
            """)
    List<Organisation> findFollowedOrganisationsWithEventsById(@Param("id") Long id);
}
