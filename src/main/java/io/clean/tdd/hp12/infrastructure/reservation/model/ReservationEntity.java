package io.clean.tdd.hp12.infrastructure.reservation.model;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReservationStatus status;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    Seat seat;

    @ManyToOne
    @JoinColumn(nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    Payment payment;
}
