package com.necpgame.backjava.exception;

/**
 * Исключение для ошибок интеграции с внешними системами
 * (БД, внешние API, файловая система)
 */
public class IntegrationException extends ApiException {
    
    public IntegrationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public IntegrationException(ErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }
}

