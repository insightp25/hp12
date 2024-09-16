package io.clean.tdd.hp12.integration.query_optimization;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.infrastructure.concert.ConcertJpaRepository;
import io.clean.tdd.hp12.infrastructure.concert.ConcertTitleJpaRepository;
import io.clean.tdd.hp12.infrastructure.concert.SeatJpaRepository;
import io.clean.tdd.hp12.infrastructure.concert.SeatOptionJpaRepository;
import io.clean.tdd.hp12.infrastructure.concert.entity.ConcertEntity;
import io.clean.tdd.hp12.infrastructure.concert.entity.ConcertTitleEntity;
import io.clean.tdd.hp12.infrastructure.concert.entity.SeatEntity;
import io.clean.tdd.hp12.infrastructure.concert.entity.SeatOptionEntity;
import io.clean.tdd.hp12.infrastructure.reservation.PaymentJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.ReservationJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.entity.PaymentEntity;
import io.clean.tdd.hp12.infrastructure.reservation.entity.ReservationEntity;
import io.clean.tdd.hp12.infrastructure.user.UserJpaRepository;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryOptimizationTestDataGenerationHelper {

    private final UserJpaRepository userJpaRepository;
    private final ConcertTitleJpaRepository concertTitleJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;
    private final SeatOptionJpaRepository seatOptionJpaRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    private static final int RANDOM_SEAT_NUMBER = 1;
    private static final int NUM_SEATS_PER_USER = 2;
    private static final long STANDARD_CLASS_SEAT_PRICE = 100_000;
    private static final String RANDOM_CONCERT_TITLE = "test title";
    private static final String RANDOM_CONCERT_DESCRIPTION = "test description";
    private static final String SEAT_CLASSIFIED_AS = "STANDARD";

    private static final int NUM_RESERVATIONS = 1_000_000;
    private static final int NUM_RESERVATION_HOLD_CAPACITY = 50;

    private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.of(2024, 8, 1, 13, 0, 0).truncatedTo(ChronoUnit.SECONDS);

    public void bulkInsertReservationForIndexTest() {
        User user = generateUser(); // 하나만 있어도 테스트에 영향 없음
        ConcertTitle concertTitle = generateConcertTitle(RANDOM_CONCERT_TITLE, RANDOM_CONCERT_DESCRIPTION); // 하나만 있어도 테스트에 영향 없음
        Concert concert = generateConcert(BASE_DATE_TIME, concertTitle); // 하나만 있어도 테스트에 영향 없음
        SeatOption seatOption = generateSeatOption(SEAT_CLASSIFIED_AS, STANDARD_CLASS_SEAT_PRICE); // 하나만 있어도 테스트에 영향 없음
        Seat seat = generateSeat(SeatStatus.OCCUPIED, RANDOM_SEAT_NUMBER, seatOption, concert); // 하나만 있어도 테스트에 영향 없음
        Payment payment = generatePayment(seatOption.price() * NUM_SEATS_PER_USER,
            PaymentStatus.COMPLETE, BASE_DATE_TIME, user);// 하나만 있어도 테스트에 영향 없음

        for (int i = 0; i < NUM_RESERVATIONS; i++) {
            long randomSecond0to300 = (long) (300 * Math.random());

            ReservationStatus status = (Math.random() < 0.8) ? ReservationStatus.FINALIZED : ReservationStatus.ABOLISHED;

            generateReservation(
                status,
                BASE_DATE_TIME
                    .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES)
                    .minusSeconds(i + randomSecond0to300),
                seat,
                user,
                payment);
        }

        for (int i = 0; i < NUM_RESERVATION_HOLD_CAPACITY; i++) {
            long randomSecond0to5 = (long) (5 * Math.random());

            generateReservation(
                ReservationStatus.ON_HOLD,
                BASE_DATE_TIME.minusSeconds(i + randomSecond0to5),
                seat,
                user,
                payment);
        }
    }

    private User generateUser() {
        return userJpaRepository.save(new UserEntity()).toModel();
    }

    private ConcertTitle generateConcertTitle(String title, String description) {
        return concertTitleJpaRepository.save(
                ConcertTitleEntity.from(ConcertTitle.builder()
                    .title(title)
                    .description(description)
                    .build()))
            .toModel();
    }

    private SeatOption generateSeatOption(String classifiedAs, long price) {
        return seatOptionJpaRepository.save(
                SeatOptionEntity.from(SeatOption.builder()
                    .classifiedAs(classifiedAs)
                    .price(price)
                    .build()))
            .toModel();
    }

    private Seat generateSeat(SeatStatus status, int seatNumber, SeatOption seatOption, Concert concert) {
        return seatJpaRepository.save(SeatEntity.from(Seat.builder()
                .status(status) // 테스트에 영향 없음
                .seatNumber(seatNumber)
                .seatOption(seatOption)
                .concert(concert)
                .build()))
            .toModel();
    }

    private Concert generateConcert(LocalDateTime occasion, ConcertTitle concertTitle) {
        return concertJpaRepository.save(ConcertEntity.from(Concert.builder()
                .occasion(occasion)
                .concertTitle(concertTitle)
                .build()))
            .toModel();
    }

    private Reservation generateReservation(ReservationStatus status, LocalDateTime createdAt, Seat seat, User user, Payment payment) {
        return reservationJpaRepository.save(ReservationEntity.from(Reservation.builder()
                .status(status)
                .createdAt(createdAt)
                .seat(seat)
                .user(user) // 테스트에 영향 없음
                .payment(payment)
                .build()))
            .toModel();
    }

    private Payment generatePayment(long amount, PaymentStatus status, LocalDateTime createdAt, User user) {
        return paymentJpaRepository.save(PaymentEntity.from(Payment.builder()
            .amount(amount)
            .status(status)
            .createdAt(createdAt)
            .user(user)
            .build())).toModel();
    }
}
