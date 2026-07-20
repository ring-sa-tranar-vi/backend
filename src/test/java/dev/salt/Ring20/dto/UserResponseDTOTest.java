package dev.salt.Ring20.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserResponseDTO Tests")
class UserResponseDTOTest {

    @Test
    void recordStoresValues() {
        UserResponseDto dto = new UserResponseDto(1L, "Jane", 4, "context", true, 7L, "Stockholm");

        assertEquals("Jane", dto.name());
        assertEquals(4, dto.intensityLevel());
        assertEquals("context", dto.context());
        assertTrue(dto.isAdmin());
        assertEquals(7L, dto.trainerId());
        assertEquals("Stockholm", dto.city());
    }
}
