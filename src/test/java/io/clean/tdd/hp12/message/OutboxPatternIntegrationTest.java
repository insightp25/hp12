package io.clean.tdd.hp12.message;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.ConcertRepository;
import io.clean.tdd.hp12.domain.concert.port.ConcertTitleRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatOptionRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.domain.point.port.PointRepository;
import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import io.clean.tdd.hp12.domain.reservation.ReservationService;
import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.PaymentRepository;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import io.clean.tdd.hp12.infrastructure.reservation.ReservationOutboxJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.enums.ReservationOutboxStatus;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OutboxPatternIntegrationTest {

    @Autowired
    private ReservationOutboxJpaRepository reservationOutboxJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ConcertTitleRepository concertTitleRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatOptionRepository seatOptionRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @Autowired
    private ReservationService reservationService;

    @Test
    void 예약_확정_및_완료시_메시지_발행_및_구독을_통해_외부로_데이터를_전송하고_아웃박스를_업데이트할_수_있다() throws InterruptedException {
        //given
        User user = userRepository.save(User.builder()
            .build());
        Point point = pointRepository.save(Point.builder()
            .point(1_000_000L)
            .updatedAt(LocalDateTime.now().minusDays(7).truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build());
        Payment payment = paymentRepository.save(Payment.builder()
            .amount(200_000L)
            .status(PaymentStatus.PENDING)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build());
        ConcertTitle concertTitle = concertTitleRepository.save(ConcertTitle.builder()
            .title("인천펜타포트 락 페스티벌")
            .description("대한민국을 대표하는 글로벌 문화관광축제 인천펜타포트 락 페스티벌! 3일간 송도달빛축제공원에서 개최됩니다.")
            .build());
        Concert concert = concertRepository.save(Concert.builder()
            .occasion(LocalDateTime.now().minusDays(7))
            .concertTitle(concertTitle)
            .build());
        SeatOption seatOption = seatOptionRepository.save(SeatOption.builder()
            .price(100_000L)
            .classifiedAs("STANDARD")
            .build());
        Seat seat1 = seatRepository.save(Seat.builder()
            .status(SeatStatus.ON_HOLD)
            .seatNumber(1)
            .seatOption(seatOption)
            .concert(concert)
            .build());
        Seat seat2 = seatRepository.save(Seat.builder()
            .status(SeatStatus.ON_HOLD)
            .seatNumber(2)
            .seatOption(seatOption)
            .concert(concert)
            .build());
        Seat seat3 = seatRepository.save(Seat.builder()
            .status(SeatStatus.ON_HOLD)
            .seatNumber(3)
            .seatOption(seatOption)
            .concert(concert)
            .build());
        Reservation pendingSeat1Reservation = reservationRepository.save(Reservation.builder()
            .status(ReservationStatus.ON_HOLD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build());
        Reservation pendingSeat2Reservation = reservationRepository.save(Reservation.builder()
            .status(ReservationStatus.ON_HOLD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat2)
            .user(user)
            .payment(payment)
            .build());
        Reservation pendingSeat3Reservation = reservationRepository.save(Reservation.builder()
            .status(ReservationStatus.ON_HOLD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat3)
            .user(user)
            .payment(payment)
            .build());
        WaitingQueue token = waitingQueueRepository.save(WaitingQueue.builder()
            .accessKey(UUID.randomUUID().toString())
            .status(WaitingQueueStatus.ACTIVE)
            .createdAt(LocalDateTime.now().minusMinutes(5))
            .lastAccessAt(LocalDateTime.now())
            .expireAt(LocalDateTime.now().plusMinutes(BusinessPolicies.ACTIVE_TOKEN_DURATION_MINUTES - 5))
            .user(user)
            .build());

        //when
        reservationService.finalize(user.id(), payment.id(), token.accessKey()); //실제 service logic 호출

        Thread.sleep(10_000); //10초 동안 producer 가 kafka message 를 발행하고 consumer 가 message 를 실행할 때까지 대기

        ReservationOutboxEntity result1 = reservationOutboxJpaRepository.findByReservationEntity_Id(pendingSeat1Reservation.id()); //검증 위해 outbox 조회
        ReservationOutboxEntity result2 = reservationOutboxJpaRepository.findByReservationEntity_Id(pendingSeat2Reservation.id()); //검증 위해 outbox 조회
        ReservationOutboxEntity result3 = reservationOutboxJpaRepository.findByReservationEntity_Id(pendingSeat3Reservation.id()); //검증 위해 outbox 조회

        //then
        // outbox 의 message 상태 created -> published 변경 검증
        Assertions.assertThat(result1.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);
        Assertions.assertThat(result2.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);
        Assertions.assertThat(result3.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);

        // 메시지에 담은 예약정보가 outbox message payload 의 예약정보와 동일한지 검증
        Assertions.assertThat(result1.getReservationEntity().getId()).isEqualTo(pendingSeat1Reservation.id());
        Assertions.assertThat(result2.getReservationEntity().getId()).isEqualTo(pendingSeat2Reservation.id());
        Assertions.assertThat(result3.getReservationEntity().getId()).isEqualTo(pendingSeat3Reservation.id());

        // message 로 발송된 예약 정보가 비즈니스 로직이 제대로 수행된 정보인지 검증
        Assertions.assertThat(result1.getReservationEntity().getStatus()).isEqualTo(ReservationStatus.FINALIZED);
        Assertions.assertThat(result2.getReservationEntity().getStatus()).isEqualTo(ReservationStatus.FINALIZED);
        Assertions.assertThat(result3.getReservationEntity().getStatus()).isEqualTo(ReservationStatus.FINALIZED);
    }
}