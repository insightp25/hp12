package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.ReservationOutboxRepository;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationOutboxRepositoryImpl implements ReservationOutboxRepository {

    private final ReservationOutboxJpaRepository reservationOutboxJpaRepository;

    @Override
    public void saveOutOf(Reservation reservation) {
        ReservationOutboxEntity reservationOutboxEntity = ReservationOutboxEntity.issueOutOf(reservation);
        reservationOutboxJpaRepository.save(reservationOutboxEntity);
    }
}