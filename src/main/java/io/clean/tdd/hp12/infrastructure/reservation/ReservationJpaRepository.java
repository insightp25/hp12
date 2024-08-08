package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByPayment_Id(long paymentId);
}
