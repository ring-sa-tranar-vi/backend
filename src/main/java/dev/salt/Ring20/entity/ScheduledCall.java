package dev.salt.Ring20.entity;

import jakarta.persistence.*;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ScheduledCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long trainerId;

    private Instant targetTime;

    private String fcmToken;

    @Enumerated(EnumType.STRING)
    private CallBackStatus callBackStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "callback_preference_id", nullable = true, foreignKey = @ForeignKey(name = "fk_scheduled_call_callback_preference"))
    private CallbackPreference callbackPreference;
}
