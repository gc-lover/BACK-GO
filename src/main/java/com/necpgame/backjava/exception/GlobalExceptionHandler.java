package com.necpgame.backjava.exception;

import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.ErrorError;
import com.necpgame.backjava.model.ErrorErrorDetailsInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

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
        
        // Добавляем детали для отладки
        List<ErrorErrorDetailsInner> details = new ArrayList<>();
        if (ex.getCause() != null) {
            ErrorErrorDetailsInner detail = new ErrorErrorDetailsInner();
            detail.setMessage("Cause: " + ex.getCause().getMessage());
            details.add(detail);
        }
        errorDetails.setDetails(details);
        
        error.setError(errorDetails);
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(error);
    }
    
    /**
     * Обработка ошибок валидации (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Error error = new Error();
        ErrorError errorDetails = new ErrorError();
        errorDetails.setCode("VAL_001");
        errorDetails.setMessage("Validation failed");
        
        // Собираем все ошибки валидации
        List<ErrorErrorDetailsInner> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            ErrorErrorDetailsInner detail = new ErrorErrorDetailsInner();
            detail.setField(fieldError.getField());
            detail.setMessage(fieldError.getDefaultMessage());
            detail.setCode("VALIDATION_ERROR");
            details.add(detail);
            log.warn("Validation error - field: {}, message: {}", fieldError.getField(), fieldError.getDefaultMessage());
        });
        
        errorDetails.setDetails(details);
        error.setError(errorDetails);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    
    /**
     * Обработка ошибок десериализации JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("JSON parse error: {}", ex.getMessage());
        
        Error error = new Error();
        ErrorError errorDetails = new ErrorError();
        errorDetails.setCode("VAL_001");
        errorDetails.setMessage("Invalid JSON format");
        
        List<ErrorErrorDetailsInner> details = new ArrayList<>();
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        ErrorErrorDetailsInner detail = new ErrorErrorDetailsInner();
        detail.setMessage(rootMessage);
        detail.setCode("JSON_PARSE_ERROR");
        details.add(detail);
        errorDetails.setDetails(details);
        
        error.setError(errorDetails);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    
    /**
     * Обработка непредвиденных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleUnexpectedException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        Error error = new Error();
        
        ErrorError errorDetails = new ErrorError();
        errorDetails.setCode("INTERNAL_ERROR");
        errorDetails.setMessage("Internal server error");
        
        // Добавляем детали для отладки (в production можно отключить)
        List<ErrorErrorDetailsInner> details = new ArrayList<>();
        
        ErrorErrorDetailsInner exDetail = new ErrorErrorDetailsInner();
        exDetail.setMessage("Exception: " + ex.getClass().getSimpleName());
        details.add(exDetail);
        
        ErrorErrorDetailsInner msgDetail = new ErrorErrorDetailsInner();
        msgDetail.setMessage("Message: " + ex.getMessage());
        details.add(msgDetail);
        
        if (ex.getCause() != null) {
            ErrorErrorDetailsInner causeDetail = new ErrorErrorDetailsInner();
            causeDetail.setMessage("Cause: " + ex.getCause().getMessage());
            details.add(causeDetail);
        }
        
        // Добавляем первые несколько строк stack trace
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            ErrorErrorDetailsInner stackDetail1 = new ErrorErrorDetailsInner();
            stackDetail1.setMessage("At: " + stackTrace[0].toString());
            details.add(stackDetail1);
            
            if (stackTrace.length > 1) {
                ErrorErrorDetailsInner stackDetail2 = new ErrorErrorDetailsInner();
                stackDetail2.setMessage("   " + stackTrace[1].toString());
                details.add(stackDetail2);
            }
        }
        
        errorDetails.setDetails(details);
        error.setError(errorDetails);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
