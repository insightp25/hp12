package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByPaymentEntity_Id(long paymentId);

    @Query("SELECT r FROM ReservationEntity r WHERE createdAt > :abolishTimestampFrom AND r.createdAt <= :abolishTimestampUntil AND r.status = :status")
    List<ReservationEntity> findAllByStatusAndCreatedAtLessThanEqual(
        @Param("abolishTimestampFrom") LocalDateTime abolishTimestampFrom,
        @Param("abolishTimestampUntil") LocalDateTime abolishTimestampUntil,
        @Param("status") ReservationStatus status);
}
