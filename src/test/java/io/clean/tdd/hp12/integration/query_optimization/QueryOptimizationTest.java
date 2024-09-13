package io.clean.tdd.hp12.integration.query_optimization;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.port.ReservationRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled // 쿼리 최적화 테스트 필요시에 한하여 개별 수행
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class QueryOptimizationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.of(2024, 8, 1, 13, 0, 0).truncatedTo(
        ChronoUnit.SECONDS);

    @Test
    void 임시예약_상태이고_예약시간이_만료된_모든_로우를_인덱스_없이_탐색하고_수행시간을_출력한다() {
        long startTime = System.currentTimeMillis();

        reservationRepository.findAllByCreatedAtBetweenAndStatus(
            BASE_DATE_TIME
                .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES +
                    BusinessPolicies.TEMPORARY_RESERVATION_ABOLISH_DEFER_MINUTES)
                .truncatedTo(ChronoUnit.SECONDS),
            BASE_DATE_TIME
                .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES)
                .truncatedTo(ChronoUnit.SECONDS),
            ReservationStatus.ON_HOLD);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        //실행 시간: 584 milliseconds
        System.out.println("실행 시간: " + duration + " milliseconds");
    }

    @Test
    void 임시예약_상태이고_예약시간이_만료된_모든_로우를_인덱스를_추가한_후_탐색하고_수행시간을_출력한다() {
        long startTime = System.currentTimeMillis();

        reservationRepository.findAllByCreatedAtBetweenAndStatus(
            BASE_DATE_TIME
                .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES +
                    BusinessPolicies.TEMPORARY_RESERVATION_ABOLISH_DEFER_MINUTES)
                .truncatedTo(ChronoUnit.SECONDS),
            BASE_DATE_TIME
                .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES)
                .truncatedTo(ChronoUnit.SECONDS),
            ReservationStatus.ON_HOLD);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        //실행 시간: 117 milliseconds
        System.out.println("실행 시간: " + duration + " milliseconds");
    }
}
