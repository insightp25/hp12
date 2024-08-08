package io.clean.tdd.hp12.infrastructure.queue.model;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_queue")
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
    @JoinColumn(nullable = false)
    User user;
}
