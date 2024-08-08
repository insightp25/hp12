package io.clean.tdd.hp12.infrastructure.queue;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.infrastructure.queue.entity.WaitingQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueueEntity, Long> {

    Optional<WaitingQueueEntity> findOptionalByAccessKey(String accessKey);

    int countByStatus(WaitingQueueStatus status);

    WaitingQueueEntity findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);

    WaitingQueueEntity findByUser_Id(long userId);
}
