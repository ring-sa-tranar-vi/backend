package dev.salt.Ring20.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Entity Tests")
class UserEntityTest {

    @Test
    void constructorSetsDefaults() {
        User user = new User("Jane", 2, "context", "clerk_1");

        assertEquals(UserRole.USER, user.getRole());
        assertEquals(1L, user.getTrainerId());
    }

    @Test
    void gettersAndSettersWork() {
        User user = new User();
        user.setName("Jane");
        user.setIntensityLevel(3);

        assertEquals("Jane", user.getName());
        assertEquals(3, user.getIntensityLevel());
    }
}
