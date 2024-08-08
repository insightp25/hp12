package io.clean.tdd.hp12.domain.queue.port;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WaitingQueueRepository {

    WaitingQueue getByAccessKey(String accessKey);

    int getStatusCount(WaitingQueueStatus status);

    WaitingQueue save(WaitingQueue waitingQueue);

    void update(WaitingQueue token);

    WaitingQueue findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);

    WaitingQueue findByUserId(long userId);

    Optional<WaitingQueue> findByAccessKey(String accessKey);

    void bulkExpire(LocalDateTime localDateTime);
}
