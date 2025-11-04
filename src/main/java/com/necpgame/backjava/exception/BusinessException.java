package com.necpgame.backjava.exception;

/**
 * Исключение для бизнес-логики приложения
 * (не найден ресурс, конфликты, лимиты)
 */
public class BusinessException extends ApiException {
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }
}

