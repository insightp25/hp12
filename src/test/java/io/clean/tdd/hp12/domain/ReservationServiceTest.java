package io.clean.tdd.hp12.domain;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ReservationServiceTest {

    private static final AtomicLong SEAT_ID_GENERATOR = new AtomicLong(1);

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private WaitingQueueRepository waitingQueueRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        SEAT_ID_GENERATOR.set(1);
    }

    @Test
    void 사용자_정보와_콘서트_정보와_좌석_정보를_입력받아_임시예약_상태를_저장하고_결제를_위한_정보를_생성할_수_있다() {
        //given
        User user = User.builder()
            .id(1L)
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
            .status(SeatStatus.AVAILABLE)
            .seatNumber(1)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat2 = Seat.builder()
            .id(2L)
            .status(SeatStatus.AVAILABLE)
            .seatNumber(2)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat3 = Seat.builder()
            .id(3L)
            .status(SeatStatus.AVAILABLE)
            .seatNumber(3)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat1OnHold = Seat.builder()
            .id(1L)
            .status(SeatStatus.ON_HOLD)
            .seatNumber(1)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat2OnHold = Seat.builder()
            .id(2L)
            .status(SeatStatus.ON_HOLD)
            .seatNumber(2)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Seat seat3OnHold = Seat.builder()
            .id(3L)
            .status(SeatStatus.ON_HOLD)
            .seatNumber(3)
            .seatOption(seatOption)
            .concert(concert)
            .build();
        Payment payment = Payment.builder()
            .id(1L)
            .amount(300_000L)
            .status(PaymentStatus.PENDING)
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

        //1. seat: 예약을 희망하는 좌석(들)을 임시 점유 처리한다
        BDDMockito.given(seatRepository.findByConcertIdAndSeatNumber(concert.id(), seat1.seatNumber())).willReturn(seat1);
        BDDMockito.given(seatRepository.findByConcertIdAndSeatNumber(concert.id(), seat2.seatNumber())).willReturn(seat2);
        BDDMockito.given(seatRepository.findByConcertIdAndSeatNumber(concert.id(), seat3.seatNumber())).willReturn(seat3);
        BDDMockito.given(seatRepository.save(seat1OnHold)).willReturn(seat1OnHold);
        BDDMockito.given(seatRepository.save(seat2OnHold)).willReturn(seat2OnHold);
        BDDMockito.given(seatRepository.save(seat3OnHold)).willReturn(seat3OnHold);

        //2. 결제 정보를 생성후 저장한다
        BDDMockito.given(userRepository.getById(anyLong())).willReturn(user);
        BDDMockito.given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        //3. 임시 예약하는 현재 시점에서 대기열의 만료 시간을 정책시간 만큼 업데이트 한다.
        BDDMockito.given(waitingQueueRepository.save(any(WaitingQueue.class))).willReturn(null);
        BDDMockito.given(waitingQueueRepository.findByUserId(anyLong())).willReturn(token);

        //4. 임시 예약 정보를 생성후 저장한다
        BDDMockito.given(reservationRepository.save(any(Reservation.class))).willAnswer(invocation -> invocation.<Reservation>getArgument(0));

        //when
        List<Reservation> result = reservationService.hold(user.id(), concert.id(), new ArrayList<>(Arrays.asList(1, 2, 3)));

        //then
        assertAll(
            () -> assertThat(result).hasSize(3),
            () -> assertThat(result.get(0).status()).isEqualTo(ReservationStatus.ON_HOLD),
            () -> assertThat(result.get(1).status()).isEqualTo(ReservationStatus.ON_HOLD),
            () -> assertThat(result.get(2).status()).isEqualTo(ReservationStatus.ON_HOLD),
            () -> assertThat(result.get(0).seat().status()).isEqualTo(SeatStatus.ON_HOLD),
            () -> assertThat(result.get(1).seat().status()).isEqualTo(SeatStatus.ON_HOLD),
            () -> assertThat(result.get(2).seat().status()).isEqualTo(SeatStatus.ON_HOLD),

            () -> assertThat(result.get(0).payment().amount()).isEqualTo(300_000L),
            () -> assertThat(result.get(1).payment().amount()).isEqualTo(300_000L),
            () -> assertThat(result.get(2).payment().amount()).isEqualTo(300_000L),
            () -> assertThat(result.get(0).payment().status()).isEqualTo(PaymentStatus.PENDING),
            () -> assertThat(result.get(1).payment().status()).isEqualTo(PaymentStatus.PENDING),
            () -> assertThat(result.get(2).payment().status()).isEqualTo(PaymentStatus.PENDING),

            () -> assertThat(result.get(0).user()).isEqualTo(User.builder().id(1L).build()),
            () -> assertThat(result.get(1).user()).isEqualTo(User.builder().id(1L).build()),
            () -> assertThat(result.get(2).user()).isEqualTo(User.builder().id(1L).build())
        );
    }
}
