package com.fasilkom.pengumpulmbkm.config;

import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.util.ResponseUtil;
import com.yahya.commonlogger.StructuredLogger;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final StructuredLogger structuredLogger;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> handleConstraintViolationException(ConstraintViolationException e) {
        structuredLogger.newLog()
                .withLogLevel(LogLevel.ERROR)
                .onFailure(e, 0);
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleAllExceptions(Exception e) {
        structuredLogger.newLog()
                .withLogLevel(LogLevel.ERROR)
                .onFailure(e, 0);
        return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
    }
}
