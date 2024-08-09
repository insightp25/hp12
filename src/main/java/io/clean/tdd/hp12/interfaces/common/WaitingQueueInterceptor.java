package io.clean.tdd.hp12.interfaces.common;

import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import io.clean.tdd.hp12.domain.queue.WaitingQueueService;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class WaitingQueueInterceptor implements HandlerInterceptor {

    private final WaitingQueueService waitingQueueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws CustomException {

        String accessKey = request.getHeader("Authorization");
        WaitingQueue token = waitingQueueService.get(accessKey);
        
        if (accessKey.isEmpty() || token == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_ERROR);
        }
        token.verifyExpiration();

        if (token.isActivated()) {
            return true;
        }

        waitingQueueService.activate(token);

        return true;
    }
}
