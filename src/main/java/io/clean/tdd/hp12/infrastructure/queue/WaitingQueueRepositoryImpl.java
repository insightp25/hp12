package io.clean.tdd.hp12.infrastructure.queue;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public WaitingQueue getByAccessKey(String accessKey) {
        return waitingQueueJpaRepository.findByAccessKey(accessKey);
    }

    @Override
    public int getStatusCount(WaitingQueueStatus status) {
        return waitingQueueJpaRepository.countByStatus(status);
    }

    @Override
    public WaitingQueue save(WaitingQueue waitingQueue) {
        return waitingQueueJpaRepository.save(waitingQueue);
    }

    //redundant! to be deleted
    @Override
    public void update(WaitingQueue token) {
        waitingQueueJpaRepository.save(token);
    }

    @Override
    public WaitingQueue findFirstByStatusOrderByIdAsc(WaitingQueueStatus status) {
        return waitingQueueJpaRepository.findFirstByStatusOrderByIdAsc(status);
    }

    @Override
    public WaitingQueue findByUserId(long userId) {
        return waitingQueueJpaRepository.findByUser_Id(userId);
    }

    @Override
    public Optional<WaitingQueue> findByAccessKey(String accessKey) {
        return waitingQueueJpaRepository.findByAccessKey(accessKey);
    }

    @Override
    public void bulkExpire(LocalDateTime localDateTime) {

    }
}
