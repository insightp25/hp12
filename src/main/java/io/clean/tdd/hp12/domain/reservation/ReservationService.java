package io.clean.tdd.hp12.domain.reservation;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.PaymentRepository;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final WaitingQueueRepository waitingQueueRepository;

    public List<Reservation> hold(long userId, long concertId, List<Integer> seatNumbers) {
        List<Seat> seats = seatNumbers.stream()
            .map(seatNumber -> seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber))
            .toList();
        seats.forEach(Seat::validateAvailabile);
        List<Seat> seatsOnHold = seats.stream()
            .map(Seat::hold)
            .map(seatRepository::update)
            .toList();

        User user = userRepository.getById(userId);
        long dueAmount = Payment.calculateAmount(seatsOnHold);
        Payment payment = Payment.issuePayment(user, dueAmount);
        paymentRepository.save(payment);

        WaitingQueue token = waitingQueueRepository.findByUserId(user.id());
        WaitingQueue refreshedToken = token.refreshForPayment();
        waitingQueueRepository.update(refreshedToken);

        List<Reservation> reservations = Reservation.hold(seatsOnHold, user, payment);
        reservations.forEach(reservationRepository::save);

        return reservations;
    }
}
