package io.clean.tdd.hp12.common;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StressTest {

    @Autowired
    private IntegratedDataGenerationHelper integratedDataGenerationHelper;

    @Test
    void 부하_테스트용_사전_데이터를_저장할_수_있다() {
        integratedDataGenerationHelper.bulkInsertDataForStressTest();
    }
}
