package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(ReservationEntity.from(reservation))
            .toModel();
    }

    @Override
    public List<Reservation> findByPaymentId(long paymentId) {
        return reservationJpaRepository.findByPaymentEntity_Id(paymentId).stream()
            .map(ReservationEntity::toModel)
            .toList();
    }

    @Override
    public List<Reservation> findAllByStatusAndExpireAtLessThanEqual(ReservationStatus status, LocalDateTime abolishTimestamp) {
        return reservationJpaRepository.findAllByStatusAndCreatedAtLessThanEqual(status, abolishTimestamp).stream()
            .map(ReservationEntity::toModel)
            .toList();
    }
}
