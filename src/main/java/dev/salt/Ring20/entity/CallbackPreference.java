package dev.salt.Ring20.entity;

import jakarta.persistence.*;
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
    @Column(name = "callback_day")
    private DayOfWeekType day;

    @Column(name = "callback_time")
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type")
    private RepeatType repeat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
