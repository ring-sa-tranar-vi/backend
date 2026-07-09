package dev.salt.Ring20.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserRequestDTO Tests")
class UserRequestDTOTest {

    @Test
    void recordStoresValues() {
        UserRequestDTO dto = new UserRequestDTO("Jane", 4, "context", 1L);

        assertEquals("Jane", dto.name());
        assertEquals(4, dto.intensityLevel());
        assertEquals("context", dto.context());
        assertEquals(1L, dto.trainerId());
    }
}
