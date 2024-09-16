package io.clean.tdd.hp12.interfaces.queue;

import io.clean.tdd.hp12.domain.queue.WaitingQueueService;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.interfaces.queue.request.TokenIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tokens")
@RequiredArgsConstructor
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;

    @PostMapping
    public ResponseEntity<WaitingQueue> issueOf(@RequestBody TokenIssueRequest tokenIssueRequest) {

        return ResponseEntity
            .ok()
            .body(waitingQueueService.push(tokenIssueRequest.userId()));
    }
}
