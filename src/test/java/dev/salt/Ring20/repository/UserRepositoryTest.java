package dev.salt.Ring20.repository;

import static org.junit.jupiter.api.Assertions.*;

import dev.salt.Ring20.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Test
    void saveAndFindByClerkIdWork() {
        User user = new User("Jane", 2, "context", "clerk_1");
        User saved = userRepository.save(user);

        assertTrue(saved.getId() > 0);
        assertTrue(userRepository.findByClerkId("clerk_1").isPresent());
    }
}
