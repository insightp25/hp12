package io.clean.tdd.hp12.message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.domain.point.port.PointHistoryRepository;
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
import io.clean.tdd.hp12.infrastructure.reservation.ReservationOutboxJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.enums.ReservationOutboxStatus;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class KafkaProducerConsumerTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private WaitingQueueRepository waitingQueueRepository;

    @Autowired
    private ReservationOutboxJpaRepository reservationOutboxJpaRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 예약_확정_및_완료시_메시지_발행_및_구독을_통해_외부로_데이터를_전송하고_아웃박스를_업데이트할_수_있다() throws InterruptedException {
        //given
        User user = User.builder()
            .id(1L)
            .build();
        Point point = Point.builder()
            .id(1L)
            .point(1_000_000L)
            .updatedAt(LocalDateTime.now().minusDays(7).truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
        Payment payment = Payment.builder()
            .id(1L)
            .amount(200_000L)
            .status(PaymentStatus.PENDING)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
        ConcertTitle concertTitle = ConcertTitle.builder()
            .id(1L)
            .title("인천펜타포트 락 페스티벌")
            .description("대한민국을 대표하는 글로벌 문화관광축제 인천펜타포트 락 페스티벌! 3일간 송도달빛축제공원에서 개최됩니다.")
            .build();
        Concert concert = Concert.builder()
            .id(1L)
            .occasion(LocalDateTime.now().minusDays(7))
            .concertTitle(concertTitle)
            .build();
        SeatOption seatOption = SeatOption.builder()
            .id(1L)
            .price(100_000L)
            .classifiedAs("STANDARD")
            .build();
        Seat seat1 = Seat.builder()
            .id(1L)
            .status(SeatStatus.OCCUPIED)
            .seatNumber(1)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat2 = Seat.builder()
            .id(2L)
            .status(SeatStatus.OCCUPIED)
            .seatNumber(2)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat3 = Seat.builder()
            .id(3L)
            .status(SeatStatus.OCCUPIED)
            .seatNumber(3)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Reservation pendingSeat1Reservation = Reservation.builder()
            .id(1L)
            .status(ReservationStatus.ON_HOLD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build();
        Reservation pendingSeat2Reservation = Reservation.builder()
            .id(2L)
            .status(ReservationStatus.ON_HOLD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build();
        Reservation pendingSeat3Reservation = Reservation.builder()
            .id(3L)
            .status(ReservationStatus.ON_HOLD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build();
        Reservation finalizedSeat1Reservation = Reservation.builder()
            .id(1L)
            .status(ReservationStatus.FINALIZED)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build();
        Reservation finalizedSeat2Reservation = Reservation.builder()
            .id(2L)
            .status(ReservationStatus.FINALIZED)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build();
        Reservation finalizedSeat3Reservation = Reservation.builder()
            .id(3L)
            .status(ReservationStatus.FINALIZED)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .seat(seat1)
            .user(user)
            .payment(payment)
            .build();
        WaitingQueue token = WaitingQueue.builder()
            .id(1L)
            .accessKey(UUID.randomUUID().toString())
            .status(WaitingQueueStatus.ACTIVE)
            .createdAt(LocalDateTime.now().minusMinutes(5))
            .lastAccessAt(LocalDateTime.now())
            .expireAt(LocalDateTime.now().plusMinutes(BusinessPolicies.ACTIVE_TOKEN_DURATION_MINUTES - 5))
            .user(user)
            .build();

        BDDMockito.given(pointRepository.getByUserId(anyLong()))
            .willReturn(point);
        BDDMockito.given(paymentRepository.findById(anyLong()))
            .willReturn(payment);
        BDDMockito.given(pointRepository.save(any()))
            .willAnswer(invocation -> invocation.<Point>getArgument(0));
        BDDMockito.willDoNothing().given(pointHistoryRepository.save(any()));
        BDDMockito.given(reservationRepository.findByPaymentId(anyLong()))
            .willReturn(Arrays.asList(pendingSeat1Reservation, pendingSeat2Reservation, pendingSeat3Reservation));
        BDDMockito.given(reservationRepository.save(pendingSeat1Reservation))
            .willReturn(finalizedSeat1Reservation);
        BDDMockito.given(reservationRepository.save(pendingSeat2Reservation))
            .willReturn(finalizedSeat2Reservation);
        BDDMockito.given(reservationRepository.save(pendingSeat3Reservation))
            .willReturn(finalizedSeat3Reservation);
        BDDMockito.willDoNothing().given(seatRepository.save(any()));
        BDDMockito.given(waitingQueueRepository.getByAccessKey(anyString()))
            .willReturn(token);
        BDDMockito.willDoNothing().given(waitingQueueRepository.save(any()));

        //when
        reservationService.finalize(user.id(), payment.id(), token.accessKey());

        Thread.sleep(3000); //3초 동안 producer 가 kafka message 를 발행하고 consumer 가 message 를 실행할 때까지 대기

        ReservationOutboxEntity result1 = reservationOutboxJpaRepository.findByReservationEntity_Id(1L);
        ReservationOutboxEntity result2 = reservationOutboxJpaRepository.findByReservationEntity_Id(2L);
        ReservationOutboxEntity result3 = reservationOutboxJpaRepository.findByReservationEntity_Id(3L);

        //then
        Assertions.assertThat(result1.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);
        Assertions.assertThat(result2.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);
        Assertions.assertThat(result3.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);
        Assertions.assertThat(result1.getReservationEntity().getId()).isEqualTo(1L);
        Assertions.assertThat(result2.getReservationEntity().getId()).isEqualTo(2L);
        Assertions.assertThat(result3.getReservationEntity().getId()).isEqualTo(3L);
        Assertions.assertThat(result1.getReservationEntity().getStatus()).isEqualTo(ReservationStatus.FINALIZED);
        Assertions.assertThat(result2.getReservationEntity().getStatus()).isEqualTo(ReservationStatus.FINALIZED);
        Assertions.assertThat(result3.getReservationEntity().getStatus()).isEqualTo(ReservationStatus.FINALIZED);
    }
}
