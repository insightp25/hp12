package io.clean.tdd.hp12.infrastructure.queue.entity;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_queue", indexes = {
    @Index(name = "idx_access_key", columnList = "access_key"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "composite_idx_status_pk", columnList = "status, id")
})
public class WaitingQueueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String accessKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    WaitingQueueStatus status;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime lastAccessAt;

    @Column(nullable = false)
    LocalDateTime expireAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity userEntity;

    public static WaitingQueueEntity from(WaitingQueue waitingQueue) {
        WaitingQueueEntity waitingQueueEntity = new WaitingQueueEntity();
        waitingQueueEntity.id = waitingQueue.id();
        waitingQueueEntity.accessKey = waitingQueue.accessKey();
        waitingQueueEntity.status = waitingQueue.status();
        waitingQueueEntity.createdAt = waitingQueue.createdAt();
        waitingQueueEntity.lastAccessAt = waitingQueue.lastAccessAt();
        waitingQueueEntity.expireAt = waitingQueue.expireAt();
        waitingQueueEntity.userEntity = UserEntity.from(waitingQueue.user());

        return waitingQueueEntity;
    }

    public WaitingQueue toModel() {
        return WaitingQueue.builder()
            .id(id)
            .accessKey(accessKey)
            .status(status)
            .createdAt(createdAt)
            .lastAccessAt(lastAccessAt)
            .expireAt(expireAt)
            .user(userEntity.toModel())
            .build();
    }
}
