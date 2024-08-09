package io.clean.tdd.hp12.domain.queue.port;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;

import io.clean.tdd.hp12.infrastructure.queue.entity.WaitingQueueEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitingQueueRepository {

    WaitingQueue getByAccessKey(String accessKey);

    int getStatusCount(WaitingQueueStatus status);

    WaitingQueue save(WaitingQueue waitingQueue);

    WaitingQueue findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);

    WaitingQueue findByUserId(long userId);

    List<WaitingQueue> findAllByStatusAndExpireAtLessThanEqual(WaitingQueueStatus status, LocalDateTime now);
}
