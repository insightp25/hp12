package io.clean.tdd.hp12.domain.queue.model;

import io.clean.tdd.hp12.common.BusinessPolicies;
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
    LocalDateTime expireAt,
    User user
) {
    public static WaitingQueue issueOf(User user) {
        return WaitingQueue.builder()
            .accessKey(UUID.randomUUID().toString())
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .expireAt(LocalDateTime.now().plusMinutes(BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES).truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }

    public void verifyExpiration() {
        if (status.equals(WaitingQueueStatus.EXPIRED)) {
            throw new CustomException(ErrorCode.TOKEN_STATUS_EXPIRED_ERROR);
        }
    }

    public boolean isActivated() {
        return status.equals(WaitingQueueStatus.ACTIVE);
    }

    public WaitingQueue confirmActivation(long tokenCountWaitingAhead) {
        if (status.equals(WaitingQueueStatus.WAITING)) {
            throw new CustomException(ErrorCode.TOKEN_STATUS_WAITING_ERROR, "현재 대기 순번은 %d 번째입니다.".formatted(tokenCountWaitingAhead));
        }

        return WaitingQueue.builder()
            .id(id)
            .accessKey(accessKey)
            .status(status)
            .createdAt(createdAt)
            .lastAccessAt(lastAccessAt)
            .user(user)
            .build();
    }

    public long estimateNumberOfTokensAhead(long firstTokenId) {
        return id - firstTokenId;
    }

    public boolean auditActivation(int activeStatusCount, long numberOfTokensAhead) {
        return activeStatusCount < BusinessPolicies.TOKEN_ACTIVATION_CAPACITY &&
            numberOfTokensAhead < BusinessPolicies.TOKEN_ACTIVATION_ORDER_DIFFERENCE_TOLERANCE;
    }

    public WaitingQueue refresh(boolean isActivationPermitted) {
        if (isActivationPermitted) {
            return WaitingQueue.builder()
                .id(id)
                .accessKey(accessKey)
                .status(WaitingQueueStatus.ACTIVE) // refreshed
                .createdAt(createdAt)
                .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)) // refreshed
                .expireAt(LocalDateTime.now().plusMinutes(BusinessPolicies.ACTIVE_TOKEN_DURATION_MINUTES).truncatedTo(ChronoUnit.SECONDS)) // refreshed
                .user(user)
                .build();
        } else {
            return WaitingQueue.builder()
                .id(id)
                .accessKey(accessKey)
                .status(status)
                .createdAt(createdAt)
                .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)) // refreshed
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
            .expireAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES))
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
            .expireAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }
}
