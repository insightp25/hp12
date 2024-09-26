package io.clean.tdd.hp12.facade.reservation;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.reservation.ReservationService;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.lock.DistributedLockHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final DistributedLockHandler distributedLockHandler;

    public List<Reservation> hold(long userId, long concertId, List<Integer> seatNumbers) {

        // 각 좌석에 대한 분산 락 획득
        seatNumbers.forEach(seatNumber -> distributedLockHandler.acquireLock(concertId + ":" + seatNumber));
        List<Seat> seatsOnHold;
        try {
            //1. seat: 예약을 희망하는 좌석(들)을 임시 점유 처리한다
            seatsOnHold = reservationService.holdSeats(concertId, seatNumbers);
        } finally {
            // 각 좌석에 대한 락 해제
            seatNumbers.forEach(seatNumber -> distributedLockHandler.releaseLock(concertId + ":" + seatNumber));
        }

        //2. 결제 정보 및 임시 예약 정보를 생성후 저장한다
        List<Reservation> reservations = reservationService.holdReservation(userId, seatsOnHold);

        //3. 임시 예약하는 현재 시점에서 대기열의 만료 시간을 정책시간 만큼 업데이트 한다.
        reservationService.extendWaitingQueueExpirationTime(userId);

        return reservations;
    }
}
