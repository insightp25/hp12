package io.clean.tdd.hp12.domain.queue;

import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.queue.port.WaitingQueueRepository;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueRepository waitingQueueRepository;
    private final UserRepository userRepository;

    public WaitingQueue push(long userId) {
        User user = userRepository.getById(userId);

        return waitingQueueRepository.save(WaitingQueue.issueOf(user));
    }

    //(인터셉터 사용)
    public WaitingQueue get(String accessKey) {
        return waitingQueueRepository.getByAccessKey(accessKey);
    }

    //(인터셉터 사용)
    public void activate(WaitingQueue token) {
        //1. 현재 활성중인 토큰 개수를 센다
        int activeTokenCount = waitingQueueRepository.getActiveStatusCount();

        //2. 대기중 첫번째 순서의 토큰을 가져온다
        WaitingQueue tokenFirstOnLine = waitingQueueRepository.findFirstByStatusOrderByIdAsc(WaitingQueueStatus.WAITING);
        long tokenCountWaitingAhead = token.estimateNumberOfTokensAhead(tokenFirstOnLine.id());

        //3. 해당 토큰을 활성해도 될지 검토한다(활성 최대 인원 + 활성 허용 범위)
        boolean isActivationPermitted = token.auditActivation(activeTokenCount, tokenCountWaitingAhead);

        //4. (활성이 허용범위라면)토큰을 활성화 한 후 저장한다
        WaitingQueue refreshedToken = token.refresh(isActivationPermitted);
        waitingQueueRepository.update(refreshedToken);

        //5. 위 단계에서 활성을 진행하지 못했을시 대기 순번 정보와 함께 오류를 반환한다
        refreshedToken.confirmActivation(tokenCountWaitingAhead);
    }

    //(스케쥴러 사용)
    public void bulkExpireTimedOutTokens() {
        waitingQueueRepository.bulkExpire(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
