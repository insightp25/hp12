package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.infrastructure.reservation.enums.ReservationOutboxStatus;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutboxEntity, Long> {

    ReservationOutboxEntity findByReservationEntity_Id(long reservationId);

    @Query("SELECT r FROM ReservationOutboxEntity r WHERE r.createdAt BETWEEN :timestampFrom AND :timestampUntil AND r.status = :status")
    List<ReservationOutboxEntity> findAllByCreatedAtBetweenAndStatus(
        @Param("timestampFrom") LocalDateTime timestampFrom,
        @Param("timestampUntil") LocalDateTime timestampUntil,
        @Param("status") ReservationOutboxStatus status);
}
