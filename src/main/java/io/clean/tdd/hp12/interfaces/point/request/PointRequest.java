package io.clean.tdd.hp12.interfaces.point.request;

import lombok.Builder;

@Builder
public record PointRequest(
    long userId,
    long amount
) {
}
