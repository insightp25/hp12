package io.clean.tdd.hp12.message;

import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.ConcertRepository;
import io.clean.tdd.hp12.domain.concert.port.ConcertTitleRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatOptionRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.PaymentRepository;
import io.clean.tdd.hp12.domain.reservation.port.ReservationMessageProducer;
import io.clean.tdd.hp12.domain.reservation.port.ReservationOutboxRepository;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import io.clean.tdd.hp12.infrastructure.reservation.ReservationOutboxJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.enums.ReservationOutboxStatus;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KafkaConfigurationTest {

    @Autowired
    private ReservationMessageProducer reservationMessageProducer;

    @Autowired
    private ReservationOutboxRepository reservationOutboxRepository;

    @Autowired
    private ReservationOutboxJpaRepository reservationOutboxJpaRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void 메시지를_발행할_시_정상적으로_모든_구독_로직을_처리할_수_있다() throws InterruptedException {
        //given
        User user = userRepository.save(User.builder()
            .id(1L)
            .build());
        Payment payment = paymentRepository.save(Payment.builder()
            .id(1L)
            .amount(200_000L)
            .status(PaymentStatus.PENDING)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build());
        ConcertTitle concertTitle = concertTitleRepository.save(ConcertTitle.builder()
            .id(1L)
            .title("인천펜타포트 락 페스티벌")
            .description("대한민국을 대표하는 글로벌 문화관광축제 인천펜타포트 락 페스티벌! 3일간 송도달빛축제공원에서 개최됩니다.")
            .build());
        Concert concert = concertRepository.save(Concert.builder()
            .id(1L)
            .occasion(LocalDateTime.now().minusDays(7))
            .concertTitle(concertTitle)
            .build());
        SeatOption seatOption = seatOptionRepository.save(SeatOption.builder()
            .id(1L)
            .price(100_000L)
            .classifiedAs("STANDARD")
            .build());
        Seat seat = seatRepository.save(Seat.builder()
            .id(1L)
            .status(SeatStatus.ON_HOLD)
            .seatNumber(1)
            .seatOption(seatOption)
            .concert(concert)
            .build());
        Reservation reservation = reservationRepository.save(Reservation.builder()
            .id(1L)
            .status(ReservationStatus.FINALIZED)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .seat(seat)
            .payment(payment)
            .build());

        //when
        reservationOutboxRepository.saveOutOf(reservation); //reservation outbox 저장(상태: 초기화)
        ReservationOutboxEntity result1 = reservationOutboxJpaRepository.findByReservationEntity_Id(1L); //메세지 consume 전 reservation outbox 상태 확인

        reservationMessageProducer.produceReservationMessage(reservation); //카프카 메시지 발행

        Thread.sleep(10_000); //10초 동안 producer 가 kafka message 를 발행하고 consumer 가 message 를 실행할 때까지 대기

        ReservationOutboxEntity result2 = reservationOutboxJpaRepository.findByReservationEntity_Id(1L); //메세지 consume 후 reservation outbox 상태 확인

        //then
        Assertions.assertThat(result1.getStatus()).isEqualTo(ReservationOutboxStatus.CREATED);
        Assertions.assertThat(result2.getStatus()).isEqualTo(ReservationOutboxStatus.PUBLISHED);
    }
}