package io.clean.tdd.hp12.interfaces.common;

import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import io.clean.tdd.hp12.domain.queue.WaitingQueueService;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpHeaders;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class WaitingQueueInterceptor implements HandlerInterceptor {

    private final WaitingQueueService waitingQueueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws CustomException {

        String accessKey = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessKey == null || accessKey.isEmpty()) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_ERROR);
        }

        WaitingQueue token = waitingQueueService.get(accessKey);
        if (token == null) {
            throw new CustomException(ErrorCode.NO_MATCHING_TOKEN_ERROR);
        }

        token.verifyExpiration();

        if (token.isActivated()) {
            return true;
        }

        waitingQueueService.activate(token);

        return true;
    }
}
