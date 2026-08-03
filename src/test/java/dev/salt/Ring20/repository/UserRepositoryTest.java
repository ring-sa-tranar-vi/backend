package dev.salt.Ring20.repository;

import static org.junit.jupiter.api.Assertions.*;

import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.enums.EventType;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private OrganisationRepository organisationRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void saveAndFindByClerkIdWork() {
        User user = new User("Jane", 2, "context", "clerk_1");
        User saved = userRepository.save(user);

        assertTrue(saved.getId() > 0);
        assertTrue(userRepository.findByClerkId("clerk_1").isPresent());
    }

    @Test
    void findFollowedOrganisationsWithEventsByIdLoadsTheirEvents() {
        User user = userRepository.save(new User("Jane", 2, "context", "clerk_2"));
        Organisation organisation =
                organisationRepository.save(
                        new Organisation("Community", "Local activities", "Stockholm", user));
        eventRepository.save(
                new Event(
                        "Morning walk",
                        "A calm walk",
                        LocalDateTime.of(2026, 8, 1, 9, 0),
                        organisation,
                        "Stockholm",
                        "City Park",
                        EventType.IN_PERSON));
        user.setFollowedOrganisations(new ArrayList<>(List.of(organisation)));
        userRepository.saveAndFlush(user);
        entityManager.clear();

        List<Organisation> followed =
                userRepository.findFollowedOrganisationsWithEventsById(user.getId());
        entityManager.clear();

        assertEquals(1, followed.size());
        assertEquals(1, followed.getFirst().getEvents().size());
    }
}
