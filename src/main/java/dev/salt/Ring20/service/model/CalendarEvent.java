package dev.salt.Ring20.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent {
    private String id;
    private  String type;
    private String title;
    private String description;
    private LocalDateTime time;
    private boolean completed;
}
