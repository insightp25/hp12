package io.clean.tdd.hp12.infrastructure.reservation.model;

import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus status;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    User user;
}
