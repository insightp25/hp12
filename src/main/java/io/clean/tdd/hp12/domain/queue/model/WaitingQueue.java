package io.clean.tdd.hp12.domain.queue.model;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record WaitingQueue(
    long id,
    UUID accessKey,
    WaitingQueueStatus status,
    LocalDateTime createdAt,
    LocalDateTime lastAccessAt,
    LocalDateTime activatedAt
) {
}
