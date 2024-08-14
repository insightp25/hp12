package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutboxEntity, Long> {

    ReservationOutboxEntity findByReservationEntity_Id(long reservationId);
}
