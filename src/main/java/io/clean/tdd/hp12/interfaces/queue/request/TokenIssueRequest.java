package io.clean.tdd.hp12.interfaces.queue.request;

import lombok.Builder;

@Builder
public record TokenIssueRequest(
    long userId
) {

}
