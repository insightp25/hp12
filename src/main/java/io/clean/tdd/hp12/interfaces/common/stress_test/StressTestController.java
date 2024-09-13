package io.clean.tdd.hp12.interfaces.common.stress_test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

// 부하테스트 전용
@Controller
@Deprecated
@RequiredArgsConstructor
public class StressTestController {

    private final StressTestDataGenerator stressTestDataGenerator;

    @PostMapping("/stress-test")
    public ResponseEntity<Void> data() {
        stressTestDataGenerator.bulkInsertDataForStressTest();

        return ResponseEntity
            .ok()
            .build();
    }
}
