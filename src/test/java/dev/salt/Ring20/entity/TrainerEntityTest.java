package dev.salt.Ring20.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Trainer Entity Tests")
class TrainerEntityTest {

    @Test
    void settersAndGettersWork() {
        Trainer trainer = new Trainer();
        trainer.setName("Alice");
        trainer.setLanguage("en");

        assertEquals("Alice", trainer.getName());
        assertEquals("en", trainer.getLanguage());
    }
}
