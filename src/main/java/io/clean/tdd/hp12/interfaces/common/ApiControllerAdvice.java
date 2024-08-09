package io.clean.tdd.hp12.interfaces.common;

import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import io.clean.tdd.hp12.domain.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("Custom Exception has occurred. Code={}, message={}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        log.trace(Arrays.toString(e.getStackTrace()));

        return ResponseEntity
            .status(Integer.parseInt(errorCode.getCode()))
            .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode generalError = ErrorCode.GENERAL_ERROR;

        log.error("General Error has occurred. Message={}", e.getMessage());
        log.trace(Arrays.toString(e.getStackTrace()));

        return ResponseEntity
            .status(Integer.parseInt(generalError.getCode()))
            .body(new ErrorResponse(generalError.getCode(), generalError.getMessage()));
    }
}

