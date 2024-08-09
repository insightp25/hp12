package io.clean.tdd.hp12.domain.common;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String additionalMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.additionalMessage = null;
    }

    public CustomException(ErrorCode errorCode, String additionalMessage) {
        super(errorCode.generateCustomMessage(additionalMessage));
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }
}
