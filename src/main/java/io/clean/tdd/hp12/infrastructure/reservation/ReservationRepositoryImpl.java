package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import io.clean.tdd.hp12.infrastructure.reservation.entity.ReservationEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public List<Reservation> findAllByCreatedAtBetweenAndStatus(
        LocalDateTime abolishTimestampFrom, LocalDateTime abolishTimestampUntil, ReservationStatus status) {
        return reservationJpaRepository
            .findAllByCreatedAtBetweenAndStatus(abolishTimestampFrom, abolishTimestampUntil, status)
            .stream()
            .map(ReservationEntity::toModel)
            .toList();
    }

    @Deprecated
    @Override
    public List<Reservation> findAllByUserId(long userId) {
        return reservationJpaRepository.findByUserEntity_Id(userId).stream()
            .map(ReservationEntity::toModel)
            .toList();
    }
}
