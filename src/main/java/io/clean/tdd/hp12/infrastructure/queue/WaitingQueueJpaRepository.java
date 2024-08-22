package io.clean.tdd.hp12.infrastructure.queue;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.infrastructure.queue.entity.WaitingQueueEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueueEntity, Long> {

    Optional<WaitingQueueEntity> findOptionalByAccessKey(String accessKey);

    int countByStatus(WaitingQueueStatus status);

    WaitingQueueEntity findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);

    WaitingQueueEntity findByUserEntity_Id(long userId);

    @Query("SELECT w FROM WaitingQueueEntity w WHERE w.status = :status AND w.expireAt <= :now")
    List<WaitingQueueEntity> findAllByStatusAndExpireAtLessThanEqual(
        @Param("status") WaitingQueueStatus status,
        @Param("now") LocalDateTime now);
}
