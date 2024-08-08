package io.clean.tdd.hp12.infrastructure.queue;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import io.clean.tdd.hp12.infrastructure.queue.entity.WaitingQueueEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public WaitingQueue getByAccessKey(String accessKey) {
        return waitingQueueJpaRepository.findOptionalByAccessKey(accessKey)
            .orElseThrow()
            .toModel();
    }

    @Override
    public int getStatusCount(WaitingQueueStatus status) {
        return waitingQueueJpaRepository.countByStatus(status);
    }

    @Override
    public WaitingQueue save(WaitingQueue token) {
        return waitingQueueJpaRepository.save(WaitingQueueEntity.from(token))
            .toModel();
    }

    @Override
    public WaitingQueue findFirstByStatusOrderByIdAsc(WaitingQueueStatus status) {
        return waitingQueueJpaRepository.findFirstByStatusOrderByIdAsc(status)
            .toModel();
    }

    @Override
    public WaitingQueue findByUserId(long userId) {
        return waitingQueueJpaRepository.findByUser_Id(userId)
            .toModel();
    }

    @Override
    public void bulkExpire(LocalDateTime localDateTime) {

    }
}
