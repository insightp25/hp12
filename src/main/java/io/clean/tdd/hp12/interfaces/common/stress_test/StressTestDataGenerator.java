package io.clean.tdd.hp12.interfaces.common.stress_test;

import io.clean.tdd.hp12.common.BusinessPolicies;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 부하테스트 전용 데이터 생성
@Component
@Deprecated
@RequiredArgsConstructor
public class StressTestDataGenerator {

    // dependencies
    private final ConcertTitleJpaRepository concertTitleJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;
    private final SeatOptionJpaRepository seatOptionJpaRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;


    // fixed variables
    private static final long STANDARD_CLASS_SEAT_PRICE = 100_000;
    private static final String RANDOM_CONCERT_TITLE = "test title";
    private static final String RANDOM_CONCERT_DESCRIPTION = "test description";
    private static final String SEAT_CLASSIFIED_AS = "STANDARD";
    private static final String WQ_RANDOM_ACCESS_KEY = "RANDOM_ACCESS_KEY";
    private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    //private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.of(2024, 8, 1, 13, 0, 0).truncatedTo(ChronoUnit.SECONDS);


    // blendable 1
    //private static final int CONCERT_TITLES = 50; // 콘서트 메타정보 50개
    private static final int CONCERT_TITLES = 5; // 콘서트 메타정보 50개
    private static final int CONCERTS_PER_CONCERT_TITLE = 2; // 콘서트 100개
    private static final int SEATS_PER_CONCERT = 100; // 좌석 10_000개 = 50 * 2 * 100 // 총 예약 5_000건 가능 // 예약 완료 4_000건 // 결제 완료 정보 4_000건 // 잔여 예약 1_000건 // 잔여 결제 1_000건
    private static final int NUM_SEATS_PER_USER = 2;

    // blendable 2
    private static final float TOTAL_RATIO_FACTOR = 5;
    private static final float OCCUPIED_RATIO_FACTOR = 4; // 예약불가 좌석 8_000개, 과거 콘서트 80개 8_000석 전석 매진
    private static final float AVAILABLE_RATIO_FACTOR = 1; // 예약가능 좌석 2_000개, 현재 콘서트 20개 좌석 2_000석 전석 예약 가능

    // blendable 3
    private static final float ABOLISH_TO_COMPLETE_MULTIPLY_FACTOR = 4;

    // blendable 4
    private static final int NUM_TOTAL_CONCERTS = CONCERT_TITLES * CONCERTS_PER_CONCERT_TITLE; // 100
    private static final int NUM_AVAILABLE_CONCERTS = (int) (NUM_TOTAL_CONCERTS * AVAILABLE_RATIO_FACTOR / TOTAL_RATIO_FACTOR); // 20

    // blendable 5 - 임시 예약용 변수 // 좌석 60개는 고정(business policies 의 활성토큰 capacity 에 의해) 두 수의 곱이 60이 되고 콘서트당 예약 개수가 짝수이면 ok.
    //private static final int NUM_RESERVATION_TARGET_CONCERTS = 6;
    private static final int NUM_RESERVATION_TARGET_CONCERTS = 2;
    private static final int NUM_HOLDING_RESERVATIONS_PER_CONCERT = 30;


    // 조정 -> 총 예약할 좌석 60개, 예약 가능 콘서트 n개 -> 60 / n
    //private static final float ACTIVE_TO_CAPACITY_RATIO = (float) 3 / 5; // 수동 설정
    //private static final int NUM_HOLDING_USERS = (int) (BusinessPolicies.TOKEN_ACTIVATION_CAPACITY * ACTIVE_TO_CAPACITY_RATIO); // 30 = 50 * 3 / 5
    //private static final int SEATS_TO_HOLD = NUM_HOLDING_USERS * NUM_SEATS_PER_USER; // 60 = 30 * 2
    //private static final int NUM_SEATS_TO_HOLD_PER_AVAILABLE_CONCERT = NUM_HOLDING_USERS * NUM_SEATS_PER_USER / NUM_AVAILABLE_CONCERTS ; // 상수
    //private static final int NUM_CONCERTS_TO_HOLD_SEAT_FOR = SEATS_TO_HOLD / NUM_SEATS_TO_HOLD_PER_AVAILABLE_CONCERT; // 6 = 60 / 10


    void bulkInsertDataForStressTest() { // 좌석 수 = 유저 수 * 2 = 결제 건수 * 2 = 대기열 토큰 수 * 2

        // 예약 가능한 콘서트 수가 holding 을 위한 콘서트 수보다 커야한다
        assert NUM_AVAILABLE_CONCERTS >= NUM_RESERVATION_TARGET_CONCERTS : "예약 가능 콘서트의 수 " + NUM_AVAILABLE_CONCERTS + "이(가) 임시 예약할 콘서트의 수 " + NUM_RESERVATION_TARGET_CONCERTS +" 보다 크거나 같아야 합니다.";

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

        // 40 = 50 * 4 / 5
        for (int i = 0; i < CONCERT_TITLES * OCCUPIED_RATIO_FACTOR / TOTAL_RATIO_FACTOR; i++) { // 40

            // 40 concert titles
            ConcertTitle concertTitle = generateConcertTitle(RANDOM_CONCERT_TITLE + countMeter, RANDOM_CONCERT_DESCRIPTION + countMeter); // concert title 4개 생성

            // 2 // 80 = 40 * 2
            for (int j = 0; j < CONCERTS_PER_CONCERT_TITLE; j++) { // 2 // 콘서트 타이틀당 콘서트 개수

                // 80 concerts
                LocalDateTime currentConcertOccasion = BASE_DATE_TIME.minusDays(i + j + 1); // 콘서트 개최 일시 생성 // 날짜 감소시키며 생성
                Concert concert = generateConcert(currentConcertOccasion, concertTitle); // 콘서트 생성

                // 1 user - init(반복문 최초 1회, 100 * 2회당 1회)
                User user = generateUser(); // 새 유저 생성 // 400명 생성

                // 1 payment - init(반복문 최초 1회, 100 * 2회당 1회), finalized
                LocalDateTime currentSeatOccupationOccasion = currentConcertOccasion.minusDays(7); // 새 유저의 콘서트 예약 일시 생성(콘서트 개최 일주일 이전)
                Payment payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.COMPLETE, currentSeatOccupationOccasion, user); // 결제 정보 생성

                // 1 waiting queue - init(반복문 최초 1회, 100 * 2회당 1회)
                generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.EXPIRED, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, currentSeatOccupationOccasion, user); // 만료된 대기열 토큰 데이터 생성

                // 100 // 8_000 = 40 * 2 * 100
                for (int k = 0; k < SEATS_PER_CONCERT; k++) {
                    // 0 ~ 7_999
                    countMeter++;

                    // 8_000 occupied seats
                    Seat seat = generateSeat(SeatStatus.OCCUPIED, k + 1, seatOption, concert); // 예약 완료 좌석 생성 // 예약 좌석 8_000

                    // 8_000 finalized reservations
                    generateReservation(ReservationStatus.FINALIZED, currentSeatOccupationOccasion, seat, user, payment); // 결제 정보 생성

                    // 1_000 abolished payments
                    // 루프 중 8의 배수 번 째에 취소된 결제 정보 생성
                    // 서브루프(반복 100회)에서 모수(100)가 8로 나눠 떨어지지 않으므로 의도했던 양의 정확한 데이터 생성이 불가 -> 루프 전체(반복 8_000회)의 모수(countMeter)에서 8의 mod 연산 수행하도록 변경
                    if (countMeter % (NUM_SEATS_PER_USER * ABOLISH_TO_COMPLETE_MULTIPLY_FACTOR) == 0) { // 2 * 4
                        generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.ABOLISHED, currentSeatOccupationOccasion, user); // 결제 정보 생성
                    }

                    // 2_000 abolished reservations
                    // 루프 중 4의 배수 번 째에 취소된 예약 정보 생성
                    if (k % ABOLISH_TO_COMPLETE_MULTIPLY_FACTOR == 0) {
                        generateReservation(ReservationStatus.ABOLISHED, currentSeatOccupationOccasion, seat, user, payment);
                    }

                    // 4_000 = 2 * 40 * 100 / 2 // 사용자 한 명, 결제 한 건당 좌석 예약이 2개씩, 예약이 2회 저장된 후 user 와 payment 새로 생성
                    if (k % NUM_SEATS_PER_USER != 0 && k != SEATS_PER_CONCERT - 1) { // 루프의 홀수 번 째 인덱스이고, 루프의 마지막 인덱스가 아닐 경우에만 실행
                        currentSeatOccupationOccasion = currentSeatOccupationOccasion.minusDays(1); // 사용자 와 결제 2건 마다 시간 업데이트

                        // 4_000 - 1 users
                        user = generateUser();

                        // 4_000 - 1 completed payments
                        payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.COMPLETE, currentSeatOccupationOccasion, user);

                        // 4_000 - 1 expired-status waiting queues
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

        // 10 = 50 * 1 / 5
        for (int i = 0; i < CONCERT_TITLES * AVAILABLE_RATIO_FACTOR / TOTAL_RATIO_FACTOR; i++) { // 10

            // 10 concert titles
            ConcertTitle concertTitle = generateConcertTitle(RANDOM_CONCERT_TITLE + countMeter, RANDOM_CONCERT_DESCRIPTION + countMeter);

            // 2 // 20 = 10 * 2
            for (int j = 0; j < CONCERTS_PER_CONCERT_TITLE; j++) { // 2

                // 20 concerts
                LocalDateTime currentConcertOccasion = BASE_DATE_TIME.plusDays(i + j + 1); // 콘서트 개최 일시 생성 // 날짜 증가시키며 생성
                Concert concert = generateConcert(currentConcertOccasion, concertTitle);

                // 1 user - init(반복문 최초 1회, 2 * 100회당 1회)
                User user = generateUser();

                // 1 payment - init(반복문 최초 1회, 2 * 100회당 1회), finalized
                LocalDateTime currentWaitingQueueCreatedAt = BASE_DATE_TIME.minusSeconds(1); // 날짜 감소시키며 생성
                generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.WAITING, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt.plusMinutes(
                    BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES), user); // 대기중 사용자 대기열 토큰 생성

                // 100 // 2_000 = 10 * 2 * 100
                for (int k = 0; k < SEATS_PER_CONCERT; k++) { // 2_000
                    // 8_000 ~ 10_000
                    countMeter++;

                    // 2_000 - 1 available seats
                    Seat seat = generateSeat(SeatStatus.AVAILABLE, k + 1, seatOption, concert);// 예약 가능 좌석 생성 // 2_000석

                    // 1_000 = 2 * 10 * 100 / 2 // 사용자 한 명, 결제 한 건당 좌석 예약이 2개씩, 예약이 2회 저장된 후 user 와 payment 새로 생성
                    if (k % NUM_SEATS_PER_USER != 0 && k != SEATS_PER_CONCERT - 1) { // 1_000

                        // 1_000 - 1 users
                        user = generateUser();

                        // 1_000 - 1 waiting-status waiting queues
                        currentWaitingQueueCreatedAt = currentWaitingQueueCreatedAt.minusSeconds(1); // user 와 seat 생성 2건 마다 시간 업데이트
                        generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.WAITING, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt, currentWaitingQueueCreatedAt.plusMinutes(BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES), user); // 대기중 사용자 대기열 토큰 생성
                    }
                }
            }
        }

        // 현재 active 하며 hold 상태의 예약 정보 및 연관 정보들을 생성한다.

        // 인덱스가 80 ~ 99인 예약 가능한 콘서트(콘서트 전체 0 ~ 99) 중 특정 수 만큼의 인덱스를 랜덤으로 추출한다. // 특정 수: NUM_RESERVATION_TARGET_CONCERTS
        ArrayList<Long> uniqueRandomAvailableConcertIndexesList = getRandomAvailableConcertIds();

        // 6
        for (int i = 0; i < NUM_RESERVATION_TARGET_CONCERTS; i++) { // 6

            // concertJpaRepository.findById(80 ~ 99)
            Concert concert = concertJpaRepository.findById(uniqueRandomAvailableConcertIndexesList.get(i)).orElseThrow().toModel();

            // 각 6회당 0 ~ 9번까지 10개의 좌석 추출
            List<Seat> seats = seatJpaRepository.findByConcertEntity_Id(concert.id()).stream()
                .map(SeatEntity::toModel)
                .limit(NUM_HOLDING_RESERVATIONS_PER_CONCERT) // 10
                .toList();

            // 새 유저의 콘서트 임시예약 일시 생성
            LocalDateTime currentSeatOccupationOccasion = BASE_DATE_TIME.minusSeconds(1);

            // 1 user - init(반복문 최초 1회, 10회당 1회)
            User user = generateUser();

            // 1 pending payment - init(반복문 최초 1회, 10회당 1회)
            Payment payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.PENDING, currentSeatOccupationOccasion, user); // 결제 정보 생성

            // 1 active-status waiting queue - init(반복문 최초 1회, 10회당 1회)
            generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.ACTIVE, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, BASE_DATE_TIME.plusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES), user); // 활성 대기열 토큰 데이터 생성

            // 10 // 60 = 6 * 10
            for (int j = 0; j < NUM_HOLDING_RESERVATIONS_PER_CONCERT; j++) {
                // 8_000 ~ 8_060
                countMeter++;

                // 60 hold-state seats
                Seat heldSeat = seatJpaRepository.save(SeatEntity.from(seats.get(j).hold())).toModel(); // 임시 예약 좌석 상태 변경

                // 60 hold-state reservations
                generateReservation(ReservationStatus.ON_HOLD, currentSeatOccupationOccasion, heldSeat, user, payment); // 결제 정보 생성

                // 30 = 6 * 10 / 2 // 사용자 한 명, 결제 한 건당 좌석 예약이 2개씩, 예약이 2회 저장된 후 user 와 payment, waiting queue 새로 생성
                if (j % NUM_SEATS_PER_USER != 0 && j != NUM_HOLDING_RESERVATIONS_PER_CONCERT - 1) {
                    currentSeatOccupationOccasion = currentSeatOccupationOccasion.minusSeconds(1);

                    // 30 - 1 users
                    user = generateUser();

                    // 30 - 1 pending-state payments
                    payment = generatePayment(STANDARD_CLASS_SEAT_PRICE * NUM_SEATS_PER_USER, PaymentStatus.PENDING, currentSeatOccupationOccasion, user);

                    // 30 - 1 active-state waiting queues
                    generateWaitingQueue(WQ_RANDOM_ACCESS_KEY + countMeter, WaitingQueueStatus.ACTIVE, currentSeatOccupationOccasion.minusMinutes(3), currentSeatOccupationOccasion, BASE_DATE_TIME.plusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES), user); // 새로운 활성 대기열 토큰 데이터 생성
                }
            }
        }
    }

    private static ArrayList<Long> getRandomAvailableConcertIds() {
        // 100 = 50 * 2 // 예약이 열려있는 콘서트 중 마지막 인덱스 + 1
        long concertsUpperBoundIndex = NUM_TOTAL_CONCERTS;
        // 80 = 100 * 4 / 5 // 예약이 열려있는 콘서트 중 첫 번째 인덱스
        long concertsLowerBoundIndex = (long) (concertsUpperBoundIndex * OCCUPIED_RATIO_FACTOR / TOTAL_RATIO_FACTOR);

        Set<Long> uniqueRandomAvailableConcertIndexesSet = new HashSet<>();
        Random random = new Random();

        // 6
        // 예약 가능한 콘서트 중 6개의 콘서트를 랜덤하게 뽑는다
        while (uniqueRandomAvailableConcertIndexesSet.size() < NUM_RESERVATION_TARGET_CONCERTS) {
            // random.nextLong(21 = 100 - 80 + 1) + 80
            long randomAvailableConcertIndex = random.nextLong(concertsUpperBoundIndex - concertsLowerBoundIndex) + concertsLowerBoundIndex;
            uniqueRandomAvailableConcertIndexesSet.add(randomAvailableConcertIndex);
        }

        ArrayList<Long> uniqueRandomAvailableConcertIndexesList = new ArrayList<>(uniqueRandomAvailableConcertIndexesSet);
        return uniqueRandomAvailableConcertIndexesList;
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
