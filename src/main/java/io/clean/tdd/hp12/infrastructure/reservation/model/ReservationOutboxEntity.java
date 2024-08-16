package io.clean.tdd.hp12.infrastructure.reservation.model;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
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
import java.time.temporal.ChronoUnit;
import lombok.Getter;

@Getter
@Entity
@Table(name = "reservation_outbox", indexes = {
    @Index(name = "idx_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_created_at_status", columnList = "created_at, status")
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

    LocalDateTime publishedAt;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    ReservationEntity reservationEntity;

    public static ReservationOutboxEntity issueOutOf(Reservation reservation) {
        ReservationOutboxEntity reservationOutboxEntity = new ReservationOutboxEntity();
        reservationOutboxEntity.status = ReservationOutboxStatus.CREATED;
        reservationOutboxEntity.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        reservationOutboxEntity.reservationEntity = ReservationEntity.from(reservation);

        return reservationOutboxEntity;
    }

    public ReservationOutboxEntity toPublishedStatus() {
        this.status = ReservationOutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        return this;
    }
}
