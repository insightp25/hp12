package io.clean.tdd.hp12.domain.queue;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueRepository waitingQueueRepository;
    private final UserRepository userRepository;

    public WaitingQueue get(String accessKey) {
        return waitingQueueRepository.getByAccessKey(accessKey);
    }

    public void activate(WaitingQueue token) {
        int activeTokenCount = waitingQueueRepository.getActiveStatusCount();

        WaitingQueue tokenFirstOnLine = waitingQueueRepository.findFirstByStatusOrderByIdAsc(WaitingQueueStatus.WAITING);
        long tokenCountWaitingAhead = token.estimateNumberOfTokensAhead(tokenFirstOnLine.id());

        boolean isActivationPermitted = token.auditActivation(activeTokenCount, tokenCountWaitingAhead);

        WaitingQueue refreshedToken = token.refresh(isActivationPermitted);
        waitingQueueRepository.update(refreshedToken);

        refreshedToken.verifyActivation(tokenCountWaitingAhead);
    }

    public WaitingQueue push(long userId) {
        User user = userRepository.getById(userId);

        return waitingQueueRepository.save(WaitingQueue.issueOf(user));
    }
}
