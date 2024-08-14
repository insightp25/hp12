package io.clean.tdd.hp12.infrastructure.reservation.model;

import io.clean.tdd.hp12.infrastructure.reservation.enums.ReservationOutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_outbox", indexes = {
    @Index(name = "idx_reservation_id", columnList = "reservation_id")
})
public class ReservationOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReservationOutboxStatus status;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    ReservationEntity reservationEntity;
}
