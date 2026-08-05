package dev.salt.Ring20.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;

import dev.salt.Ring20.dto.company.CreateCompanyEventDto;
import org.junit.jupiter.api.Test;

class CompanyEventDtoTimeParsingTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void acceptsLocalIsoDateTime() throws Exception {
        CreateCompanyEventDto dto =
                objectMapper.readValue(
                        createEventJson("2026-08-03T18:30:00"), CreateCompanyEventDto.class);

        assertEquals(LocalDateTime.of(2026, 8, 3, 18, 30), dto.time());
    }

    @Test
    void acceptsUtcIsoDateTime() throws Exception {
        CreateCompanyEventDto dto =
                objectMapper.readValue(
                        createEventJson("2026-08-03T18:30:00.000Z"), CreateCompanyEventDto.class);

        assertEquals(LocalDateTime.of(2026, 8, 3, 18, 30), dto.time());
    }

    private String createEventJson(String time) {
        return """
                {
                  "name": "Event",
                  "description": "Description",
                  "time": "%s",
                  "city": "Stockholm",
                  "venue": "Hall",
                  "eventType": "IN_PERSON",
                  "organisation": { "id": 1 }
                }
                """
                .formatted(time);
    }
}
