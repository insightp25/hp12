package io.clean.tdd.hp12.integration.stress_test_init;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled // 부하 테스트시에 한하여 개별 수행
@SpringBootTest
@RequiredArgsConstructor
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StressTestDataGenerator {

    private final StressTestDataGenerationHelper stressTestDataGenerationHelper;

    @Test
    void 부하_테스트용_사전_데이터를_저장할_수_있다() {
        stressTestDataGenerationHelper.bulkInsertDataForStressTest();
    }
}
