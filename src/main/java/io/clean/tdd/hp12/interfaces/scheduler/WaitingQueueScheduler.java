package io.clean.tdd.hp12.interfaces.scheduler;

import io.clean.tdd.hp12.domain.queue.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueScheduler {

    private final WaitingQueueService waitingQueueService;

    // 특정 시간 간격마다 현재를 기준으로 유효시간이 초과된 토큰을 만료시킨다.
    @Scheduled(fixedRate = 5 * 1000)
    public void expireTimedOutTokens() {
        waitingQueueService.bulkExpireTimedOutTokens();
    }
}
