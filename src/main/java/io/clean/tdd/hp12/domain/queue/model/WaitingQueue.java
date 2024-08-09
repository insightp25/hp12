package io.clean.tdd.hp12.domain.queue.model;

import io.clean.tdd.hp12.common.TokenPolicies;
import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder
public record WaitingQueue(
    long id,
    String accessKey,
    WaitingQueueStatus status,
    LocalDateTime createdAt,
    LocalDateTime lastAccessAt,
    LocalDateTime expiresAt,
    User user
) {
    public static WaitingQueue issueOf(User user) {
        return WaitingQueue.builder()
            .accessKey(UUID.randomUUID().toString())
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .expiresAt(LocalDateTime.now().plusMinutes(TokenPolicies.ISSUANCE_DURATION_MINUTES).truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }

    public void verifyExpiration() {
        if (status.equals(WaitingQueueStatus.EXPIRED)) {
            throw new CustomException(ErrorCode.TOKEN_STATUS_EXPIRED_ERROR);
        }
    }

    public void verifyActivation(long tokenCountWaitingAhead) {
        if (status.equals(WaitingQueueStatus.WAITING)) {
            throw new CustomException(ErrorCode.TOKEN_STATUS_WAITING_ERROR, "현재 대기 순번은 %d 번째입니다.".formatted(tokenCountWaitingAhead));
        }
    }

    public long estimateNumberOfTokensAhead(long firstTokenId) {
        return id - firstTokenId;
    }

    public boolean auditActivation(int activeStatusCount, long numberOfTokensAhead) {
        return activeStatusCount < TokenPolicies.ACTIVATION_CAPACITY &&
            numberOfTokensAhead < TokenPolicies.ACTIVATION_ORDER_TOLERANCE_INDEX;
    }

    public WaitingQueue refresh(boolean isActivationPermitted) {
        if (isActivationPermitted) {
            return WaitingQueue.builder()
                .id(id)
                .accessKey(accessKey)
                .status(WaitingQueueStatus.ACTIVE)
                .createdAt(createdAt)
                .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .expiresAt(LocalDateTime.now().plusMinutes(TokenPolicies.ACTIVATION_DURATION_MINUTES).truncatedTo(ChronoUnit.SECONDS))
                .user(user)
                .build();
        } else {
            return WaitingQueue.builder()
                .id(id)
                .accessKey(accessKey)
                .status(status)
                .createdAt(createdAt)
                .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .user(user)
                .build();
        }
    }

    public WaitingQueue refreshForPayment() {
        return WaitingQueue.builder()
            .id(id)
            .accessKey(accessKey)
            .status(status)
            .createdAt(createdAt)
            .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .expiresAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(TokenPolicies.PAYMENT_DURATION_MINUTES))
            .user(user)
            .build();
    }

    public WaitingQueue expire() {
        if (status == WaitingQueueStatus.EXPIRED) {
            throw new CustomException(ErrorCode.TOKEN_STATUS_EXPIRED_ERROR);
        }

        return WaitingQueue.builder()
            .id(id)
            .accessKey(accessKey)
            .status(WaitingQueueStatus.EXPIRED)
            .createdAt(createdAt)
            .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .expiresAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }
}
