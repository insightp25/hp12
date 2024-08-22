package io.clean.tdd.hp12.common;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataGenerator {

    @Autowired
    private TestDataGenerationHelper testDataGenerationHelper;

    @Disabled
    @Test
    void initData() {
        testDataGenerationHelper.bulkInsertReservationForIndexTest();
    }
}
