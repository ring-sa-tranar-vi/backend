package dev.salt.Ring20.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String value = parser.getText().trim();
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException localDateTimeException) {
            try {
                return OffsetDateTime.parse(value).toLocalDateTime();
            } catch (DateTimeParseException offsetDateTimeException) {
                return (LocalDateTime)
                        context.handleWeirdStringValue(
                                LocalDateTime.class,
                                value,
                                "Expected an ISO-8601 date-time with or without an offset");
            }
        }
    }
}
