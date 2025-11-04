package com.necpgame.backjava.exception;

/**
 * Исключение для ошибок валидации данных от клиента
 * (некорректные данные, отсутствующие поля, неверный формат)
 */
public class ValidationException extends ApiException {
    
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public ValidationException(ErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }
}

