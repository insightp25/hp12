package io.clean.tdd.hp12.domain.queue.port;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;

public interface WaitingQueueRepository {

    WaitingQueue getByAccessKey(String accessKey);

    int getActiveStatusCount();

    WaitingQueue save(WaitingQueue waitingQueue);

    void update(WaitingQueue token);

    WaitingQueue findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);

    WaitingQueue findByUserId(long userId);
}
