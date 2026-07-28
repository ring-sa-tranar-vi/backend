package dev.salt.Ring20.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrganizationApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User user;

    private String organizationName;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String motivation;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


}
