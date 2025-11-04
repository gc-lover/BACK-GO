package com.necpgame.backjava.exception;

import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.ErrorError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений
 * Преобразует доменные исключения в HTTP ответы с Error DTO из OpenAPI
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Обработка всех доменных исключений (ApiException и наследники)
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        
        log.warn("API Exception: {} - {}", errorCode.getCode(), ex.getMessage());
        
        // Создаём Error DTO из OpenAPI спецификации
        Error error = new Error();
        
        ErrorError errorDetails = new ErrorError();
        errorDetails.setCode(errorCode.getCode());
        errorDetails.setMessage(ex.getMessage());
        
        error.setError(errorDetails);
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(error);
    }
    
    /**
     * Обработка непредвиденных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleUnexpectedException(Exception ex) {
        log.error("Unexpected exception", ex);
        
        Error error = new Error();
        
        ErrorError errorDetails = new ErrorError();
        errorDetails.setCode("INTERNAL_ERROR");
        errorDetails.setMessage("Internal server error");
        
        error.setError(errorDetails);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
