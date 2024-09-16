package io.clean.tdd.hp12.integration.query_optimization;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled // 쿼리 최적화 테스트 환경 세팅을 위해 테스트 이전 최초에 한해 1회 실행
@SpringBootTest
@RequiredArgsConstructor
public class QueryOptimizationTestDataGenerator {

    private final QueryOptimizationTestDataGenerationHelper queryOptimizationTestDataGenerationHelper;

    @Test
    void initQueryOptimizationTestData() {
        queryOptimizationTestDataGenerationHelper.bulkInsertReservationForIndexTest();
    }
}
