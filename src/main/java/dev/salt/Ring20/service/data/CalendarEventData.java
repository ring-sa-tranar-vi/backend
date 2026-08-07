package dev.salt.Ring20.service.data;

import java.time.LocalDateTime;


public record CalendarEventData(String id,
                                String type,
                                String title,
                                String description,
                                LocalDateTime time,
                                boolean completed) {

}
