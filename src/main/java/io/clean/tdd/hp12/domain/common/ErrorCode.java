package io.clean.tdd.hp12.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CONCERT_NOT_FOUND_ERROR("404", "해당 콘서트가 존재하지 않습니다"),
    CONCERT_UNAVAILABLE_ERROR("409", "본 콘서트는 신청이 마감되었습니다"),

    TOKEN_STATUS_WAITING_ERROR("409", "대기 상태이므로 접근할 수 없습니다"),
    TOKEN_STATUS_EXPIRED_ERROR("409", "접근 권한이 만료되었습니다"),
    TOKEN_NOT_FOUND_ERROR("409", "접근을 위한 대기 토큰이 존재하지 않습니다"),

    SEAT_OCCUPIED_ERROR("409", "좌석이 이미 점유되어 있습니다"),

    BAD_INPUT_POINT_VALUE_ERROR("400", "잘못된 포인트 입력입니다. 입력은 0보다 큰 수이어야 합니다"),
    INSUFFICIENT_POINTS_ERROR("409", "포인트 잔액이 부족합니다"),

    GENERAL_ERROR("500", "서버 오류가 발생했습니다");

    private final String code;
    private final String message;

    public String generateCustomMessage(String additionalMessage) {
        return this.message + ": " + additionalMessage;
    }
}
