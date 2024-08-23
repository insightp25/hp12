package io.clean.tdd.hp12.common;

import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
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
import io.clean.tdd.hp12.infrastructure.queue.WaitingQueueJpaRepository;
import io.clean.tdd.hp12.infrastructure.queue.entity.WaitingQueueEntity;
import io.clean.tdd.hp12.infrastructure.reservation.PaymentJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.ReservationJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.entity.PaymentEntity;
import io.clean.tdd.hp12.infrastructure.reservation.entity.ReservationEntity;
import io.clean.tdd.hp12.infrastructure.user.UserJpaRepository;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IntegratedDataGenerationHelper {

    private final ConcertTitleJpaRepository concertTitleJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;
    private final SeatOptionJpaRepository seatOptionJpaRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    private static final int NUM_SEATS_PER_USER = 2;
    private static final long STANDARD_CLASS_SEAT_PRICE = 100_000;
    private static final String RANDOM_CONCERT_TITLE = "test title";
    private static final String RANDOM_CONCERT_DESCRIPTION = "test description";
    private static final String SEAT_CLASSIFIED_AS = "STANDARD";
    private static final String WQ_RANDOM_ACCESS_KEY = "RANDOM_ACCESS_KEY";
    private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.of(2024, 8, 1, 13, 0, 0).truncatedTo(ChronoUnit.SECONDS);

    private static final int NUM_CONCERT_TITLES = 50; // 콘서트 메타정보 50개
    private static final int CONCERT_TITLE_TO_CONCERT_MULTIPLE_FACTOR = 2; // 콘서트 100개
    private static final int SEAT_PER_CONCERT_FACTOR = 100; // 50 * 2 * 100 = 좌석 10_000개 // 총 예약 5_000건 가능 // 예약 완료 4_000건 // 결제 완료 정보 4_000건 // 잔여 예약 1_000건 // 잔여 결제 1_000건
    private static final float SEATS_AVAILABLE_RATIO_FACTOR = 1; // 예약가능 좌석 2_000개, 현재 콘서트 20개 좌석 2_000석 전석 예약 가능
    private static final float SEATS_OCCUPIED_RATIO_FACTOR = 4; // 예약불가 좌석 8_000개, 과거 콘서트 80개 8_000석 전석 매진
    private static final float SEATS_TOTAL_RATIO_FACTOR = 5;
    private static final float ABOLISH_TO_COMPLETE_MULTIPLE_FACTOR = 4;
    private static final int NUM_RESERVATION_TARGET_CONCERTS = 6;
    private static final int NUM_HOLDING_RESERVATIONS_PER_CONCERT = 10;

    void bulkInsertDataForStressTest() { // 좌석 수 = 유저 수 * 2 = 결제 건수 * 2 = 대기열 토큰 수 * 2
        assert NUM_CONCERT_TITLES * CONCERT_TITLE_TO_CONCERT_MULTIPLE_FACTOR * (SEATS_AVAILABLE_RATIO_FACTOR / SEATS_TOTAL_RATIO_FACTOR) >= NUM_RESERVATION_TARGET_CONCERTS : "예약 가능 콘서트의 수가 " + NUM_RESERVATION_TARGET_CONCERTS + "보다 커야합니다.";

        int countMeter = 0;

        SeatOption seatOption = generateSeatOption(SEAT_CLASSIFIED_AS, STANDARD_CLASS_SEAT_PRICE);

        // 과거 4/5 콘서트 및 과거 예약 완료 좌석 생성
        // 콘서트 타이틀 50 * 4 / 5 = 40
        // 콘서트 40 * 2 = 80
        // 예약 확정 좌석 수 80 * 100 = 8_000
        // 완료 예약 수 = 8_000
        // 취소 예약 수 = 2_000
        // 완료 결제 수 = 4_000
        // 취소 결제 수 = 1_000
        // 사용자 수 = 4_000
        // 토큰 수 = 4_000
        // countMeter 0 ~ 7_999
        for (int i = 0; i < NUM_CONCERT_TITLES * SEATS_OCCUPIED_RATIO_FACTOR / SEATS_TOTAL_RATIO_FACTOR; i++) { // 40
            ConcertTitle concertTitle = generateConcertTitle(RANDOM_CONCERT_TITLE + countMeter, RANDOM_CONCERT_DESCRIPTION + countMeter); // concert title 생성

            for (int j = 0; j < CONCERT_TITLE_TO_CONCERT_MULTIPLE_FACTOR; j++) { // 2
                LocalDateTime currentConcertOccasion = BASE_DATE_TIME.minusDays(i + j + 1); // 콘서트 개최 일시 생성 // 날짜 감소시키며 생성

                Concert concert = generateConcert(currentConcertOccasion, concertTitle);

                User user = generateUser(); // 새 유저 생성 // 4_000명 생성
                LocalDateTime currentSeatOccupationOccasion = currentConcertOccasion.minusDays(7); // 새 유저의 콘서트 예약 일시 생성(콘서트 개최 일주일 이전)
                Payment payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.COMPLETE, currentSeatOccupationOccasion, user); // 결제 정보 생성
                generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.EXPIRED, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, currentSeatOccupationOccasion, user); // 만료된 대기열 토큰 데이터 생성

                for (int k = 0; k < SEAT_PER_CONCERT_FACTOR; k++) { // 100
                    countMeter++;

                    Seat seat = generateSeat(SeatStatus.OCCUPIED, k + 1, seatOption, concert); // 예약 완료 좌석 생성 // 예약 좌석 8_000

                    generateReservation(ReservationStatus.FINALIZED, currentSeatOccupationOccasion, seat, user, payment); // 결제 정보 생성

//                    if (k % (NUM_SEATS_PER_USER * ABOLISH_TO_COMPLETE_MULTIPLE_FACTOR) == 0) { // 1_000 // 1/8 번 째에 취소된 결제 정보 생성 (각 루프마다 12번 생성)
//                        generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.ABOLISHED, currentSeatOccupationOccasion, user); // 결제 정보 생성
//                    }
                    if (countMeter % (NUM_SEATS_PER_USER * ABOLISH_TO_COMPLETE_MULTIPLE_FACTOR) == 0) {
                        generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.ABOLISHED, currentSeatOccupationOccasion, user); // 결제 정보 생성
                    }

                    if (k % ABOLISH_TO_COMPLETE_MULTIPLE_FACTOR == 0) { // 2_000 // 1/4 번 째에 취소된 예약 정보 생성 (각 루프마다 25번 생성 = )
                        generateReservation(ReservationStatus.ABOLISHED, currentSeatOccupationOccasion, seat, user, payment);
                    }

                    if (k % NUM_SEATS_PER_USER != 0 && k != SEAT_PER_CONCERT_FACTOR - 1) { // 4_000 // 사용자 한 명, 결제 한 건당 좌석 예약이 2개씩, 예약이 2회 저장된 후 user 와 payment 새로 생성
                        currentSeatOccupationOccasion = currentSeatOccupationOccasion.minusDays(1); // 사용자 와 결제 2건 마다 시간 업데이트

                        user = generateUser();
                        payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.COMPLETE, currentSeatOccupationOccasion, user);
                        generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.EXPIRED, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, currentSeatOccupationOccasion, user); // 새로운 만료 대기열 토큰 데이터 생성
                    }
                }
            }
        }

        // 새로운 1/5 콘서트 및 현재 ~ 미래 예약 가능 좌석 생성
        // 콘서트 타이틀 50 * 1 / 5 = 10
        // 콘서트 10 * 2 = 20
        // 좌석 수 20 * 100 = 2_000
        // 예약 수 = 2_000
        // 결제 수 = 1_000
        // 사용자 수 = 1_000
        // 토큰 수 = 1_000
        // countMeter 8_000 ~ 9_999
        for (int i = 0; i < NUM_CONCERT_TITLES * SEATS_AVAILABLE_RATIO_FACTOR / SEATS_TOTAL_RATIO_FACTOR; i++) { // 10
            ConcertTitle concertTitle = generateConcertTitle(RANDOM_CONCERT_TITLE + countMeter, RANDOM_CONCERT_DESCRIPTION + countMeter);

            for (int j = 0; j < CONCERT_TITLE_TO_CONCERT_MULTIPLE_FACTOR; j++) { // 20
                LocalDateTime currentConcertOccasion = BASE_DATE_TIME.plusDays(i + j + 1); // 콘서트 개최 일시 생성 // 날짜 증가시키며 생성

                Concert concert = generateConcert(currentConcertOccasion, concertTitle);

                User user = generateUser(); // 새 유저 생성 // 1_000명 생성
                LocalDateTime currentWaitingQueueCreatedAt = BASE_DATE_TIME.minusSeconds(1); // 날짜 감소시키며 생성
                generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.WAITING, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt.plusMinutes(BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES), user); // 대기중 사용자 대기열 토큰 생성

                for (int k = 0; k < SEAT_PER_CONCERT_FACTOR; k++) { // 2_000
                    countMeter++;

                    Seat seat = generateSeat(SeatStatus.AVAILABLE, k + 1, seatOption, concert);// 예약 가능 좌석 생성 // 2_000석

                    if (k % 2 != 0 && k != SEAT_PER_CONCERT_FACTOR - 1) { // 1_000
                        user = generateUser();
                        currentWaitingQueueCreatedAt = currentWaitingQueueCreatedAt.minusSeconds(1); // user 와 seat 생성 2건 마다 시간 업데이트
                        generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.WAITING, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt.plusMinutes(BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES), user); // 대기중 사용자 대기열 토큰 생성
                    }
                }
            }
        }

        long concertsUpperBoundIndex = NUM_CONCERT_TITLES * CONCERT_TITLE_TO_CONCERT_MULTIPLE_FACTOR; // 전체 콘서트 수
        long concertsLowerBoundIndex = (long) (concertsUpperBoundIndex * SEATS_OCCUPIED_RATIO_FACTOR / SEATS_TOTAL_RATIO_FACTOR); // 예약이 열려있는 콘서트의 첫 번째 인덱스

        Set<Long> uniqueRandomAvailableConcertIndexesSet = new HashSet<>();
        Random random = new Random();

        while (uniqueRandomAvailableConcertIndexesSet.size() < NUM_RESERVATION_TARGET_CONCERTS) {
            long randomAvailableConcertIndex = random.nextLong(concertsUpperBoundIndex - concertsLowerBoundIndex + 1) + concertsLowerBoundIndex;
            uniqueRandomAvailableConcertIndexesSet.add(randomAvailableConcertIndex);
        }

        ArrayList<Long> uniqueRandomAvailableConcertIndexesList = new ArrayList<>(uniqueRandomAvailableConcertIndexesSet);

        for (int i = 0; i < NUM_RESERVATION_TARGET_CONCERTS; i++) { // 6
            Concert concert = concertJpaRepository.findById(uniqueRandomAvailableConcertIndexesList.get(i)).orElseThrow().toModel();

            List<Seat> seats = seatJpaRepository.findByConcertEntity_Id(concert.id()).stream()
                .map(SeatEntity::toModel)
                .limit(NUM_HOLDING_RESERVATIONS_PER_CONCERT)
                .toList();

            LocalDateTime currentSeatOccupationOccasion = BASE_DATE_TIME.minusSeconds(1); // 새 유저의 콘서트 임시예약 일시 생성

            User user = generateUser();
            Payment payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.PENDING, currentSeatOccupationOccasion, user); // 결제 정보 생성
            generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.ACTIVE, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, BASE_DATE_TIME.plusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES), user); // 활성 대기열 토큰 데이터 생성

            for (int j = 0; j < NUM_HOLDING_RESERVATIONS_PER_CONCERT; j++) { // 60
                countMeter++;

                Seat heldSeat = seatJpaRepository.save(SeatEntity.from(seats.get(j).hold())).toModel(); // 임시 예약 좌석 상태 변경

                generateReservation(ReservationStatus.ON_HOLD, currentSeatOccupationOccasion, heldSeat, user, payment); // 결제 정보 생성

                if (j % NUM_SEATS_PER_USER != 0 && j != NUM_HOLDING_RESERVATIONS_PER_CONCERT - 1) {
                    currentSeatOccupationOccasion = currentSeatOccupationOccasion.minusSeconds(1);

                    user = generateUser();
                    payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.PENDING, currentSeatOccupationOccasion, user);
                    generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.ACTIVE, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, BASE_DATE_TIME.plusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES), user); // 새로운 활성 대기열 토큰 데이터 생성
                }
            }
        }
    }

    private ConcertTitle generateConcertTitle(String title, String description) {
        return concertTitleJpaRepository.save(
                ConcertTitleEntity.from(ConcertTitle.builder()
                    .title(title)
                    .description(description)
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

    private User generateUser() {
        return userJpaRepository.save(new UserEntity()).toModel();
    }

    private WaitingQueue generateWaitingQueue(String accessKey, WaitingQueueStatus status, LocalDateTime createdAt, LocalDateTime lastAccessAt, LocalDateTime expireAt, User user) {
        return waitingQueueJpaRepository.save(WaitingQueueEntity.from(WaitingQueue.builder()
                .accessKey(accessKey)
                .status(status)
                .createdAt(createdAt)
                .lastAccessAt(lastAccessAt)
                .expireAt(expireAt)
                .user(user)
            .build())).toModel();
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
