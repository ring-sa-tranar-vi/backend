package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CallbackPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeekType day;

    private LocalTime time;

    @Enumerated(EnumType.STRING)
    private RepeatType repeat;

    private LocalDate validUntil;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
