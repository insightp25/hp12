package io.clean.tdd.hp12.infrastructure.reservation.entity;

import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.infrastructure.concert.entity.SeatEntity;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "reservation", indexes = {
    @Index(name = "idx_payment_id", columnList = "payment_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
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
    @JoinColumn(name = "seat_id", nullable = false)
    SeatEntity seatEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    PaymentEntity paymentEntity;

    public static ReservationEntity from(Reservation reservation) {
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.id = reservation.id();
        reservationEntity.status = reservation.status();
        reservationEntity.createdAt = reservation.createdAt();
        reservationEntity.seatEntity = SeatEntity.from(reservation.seat());
        reservationEntity.userEntity = UserEntity.from(reservation.user());
        reservationEntity.paymentEntity = PaymentEntity.from(reservation.payment());

        return reservationEntity;
    }

    public Reservation toModel() {
        return Reservation.builder()
            .id(id)
            .status(status)
            .createdAt(createdAt)
            .seat(seatEntity.toModel())
            .user(userEntity.toModel())
            .payment(paymentEntity.toModel())
            .build();
    }
}
