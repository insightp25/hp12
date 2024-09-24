package io.clean.tdd.hp12.integration.concurrency_control;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.ConcertRepository;
import io.clean.tdd.hp12.domain.concert.port.ConcertTitleRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatOptionRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import io.clean.tdd.hp12.domain.reservation.ReservationService;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PessimisticLockTest {

    private static final int NUM_PARTICIPANTS_10 = 10;
    public static final LocalDateTime SEVEN_DAYS_AHEAD_FROM_NOW = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(7);

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WaitingQueueRepository waitingQueueRepository;
    @Autowired
    private ConcertTitleRepository concertTitleRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private SeatOptionRepository seatOptionRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void 낙관락을_적용한_동시성_테스트를_수행할_수_있다() {

        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < NUM_PARTICIPANTS_10; i++) {
            User user = userRepository.save(User.builder().build());
            users.add(user);

            waitingQueueRepository.save(WaitingQueue.builder()
                .accessKey(UUID.randomUUID().toString())
                .status(WaitingQueueStatus.ACTIVE)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .expireAt(
                    LocalDateTime.now().plusMinutes(BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES)
                        .truncatedTo(ChronoUnit.SECONDS))
                .user(user)
                .build());
        }
        ConcertTitle concertTitle = concertTitleRepository.save(ConcertTitle.builder()
            .title("인천펜타포트 락 페스티벌")
            .description("대한민국을 대표하는 글로벌 문화관광축제 인천펜타포트 락 페스티벌! 3일간 송도달빛축제공원에서 개최됩니다.")
            .build());
        Concert concert = concertRepository.save(Concert.builder()
            .occasion(SEVEN_DAYS_AHEAD_FROM_NOW)
            .concertTitle(concertTitle)
            .build());
        SeatOption seatOptionStandard = seatOptionRepository.save(SeatOption.builder()
            .classifiedAs("STANDARD")
            .price(100_000L)
            .build());
        Seat seat = seatRepository.save(Seat.builder()
            .status(SeatStatus.AVAILABLE)
            .seatNumber(1)
            .concert(concert)
            .seatOption(seatOptionStandard)
            .build());

        // when
        // 50개의 모든 스레드가 동시에 실행되도록 latch 설정
        CountDownLatch latch = new CountDownLatch(1);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // user 50명을 위한 스레드 50개
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        List<Reservation> results = new ArrayList<>();

        for (int i = 0; i < NUM_PARTICIPANTS_10; i++) {
            final User user = users.get(i);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    latch.await();

                    // 동시성 이슈 발생 메인 로직
                    List<Reservation> reservationsHeld = reservationService.hold(user.id(), concert.id(), List.of(seat.seatNumber()));

                    results.addAll(reservationsHeld);

                } catch (InterruptedException | CustomException | OptimisticLockException e) {
//                } catch (InterruptedException | CustomException | Exception e) {
//                    if (e.getClass().equals(CustomException.class)) {
//                        System.out.println(((CustomException) e).getErrorCode());
//                    }
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("예상하지 못한 예외 발생: " + e.getMessage());
                }
            }, executorService);

            futures.add(future);
        }

        // 스레드 50개 동시 시작 트리거
        latch.countDown();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        // 좌석의 상태 WAITING -> ON_HOLD 변경 검증
        Seat updatedSeat = seatRepository.findByConcertIdAndSeatNumber(concert.id(),
            seat.seatNumber());
        Assertions.assertThat(updatedSeat.status()).isEqualTo(SeatStatus.ON_HOLD);

        // 성공한 사람이 1명임을 검증
        Assertions.assertThat(results.size()).isEqualTo(1);

        // 실패한 사람이 NUM_PARTICIPANTS_10 - 1 명임을 검증
        User successfulUser = results.get(0).user();
        long failedAttempts = users.stream()
            .filter(user -> successfulUser.id() != user.id())
            .map(user -> reservationRepository.findAllByUserId(user.id()))
            .filter(List::isEmpty)  // No reservation should exist for these users
            .count();
        Assertions.assertThat(failedAttempts).isEqualTo(NUM_PARTICIPANTS_10 - 1);

        executorService.shutdown();
    }
}
