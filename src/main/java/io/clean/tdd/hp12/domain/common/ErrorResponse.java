package io.clean.tdd.hp12.domain.common;

public record ErrorResponse(
    String code,
    String message
) {
}
