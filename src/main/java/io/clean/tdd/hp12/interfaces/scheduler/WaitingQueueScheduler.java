package io.clean.tdd.hp12.interfaces.scheduler;

import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.queue.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueScheduler {

    private final WaitingQueueService waitingQueueService;

    @Scheduled(fixedRate = BusinessPolicies.EXPIRATION_SCHEDULING_INTERVAL_MINUTES * 1000)
    public void expireTimedOutTokens() {
        waitingQueueService.bulkExpireTimedOutTokens();
    }
}
