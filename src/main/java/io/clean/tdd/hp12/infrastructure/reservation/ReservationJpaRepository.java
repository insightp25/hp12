package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.infrastructure.reservation.entity.ReservationEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByPaymentEntity_Id(long paymentId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.createdAt BETWEEN :abolishTimestampFrom AND :abolishTimestampUntil AND r.status = :status")
    List<ReservationEntity> findAllByCreatedAtBetweenAndStatus(
        @Param("abolishTimestampFrom") LocalDateTime abolishTimestampFrom,
        @Param("abolishTimestampUntil") LocalDateTime abolishTimestampUntil,
        @Param("status") ReservationStatus status);

    @Deprecated
    List<ReservationEntity> findByUserEntity_Id(long userId);
}
