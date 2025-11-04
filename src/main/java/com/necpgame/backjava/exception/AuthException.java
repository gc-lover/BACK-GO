package com.necpgame.backjava.exception;

/**
 * Исключение для ошибок аутентификации и авторизации
 * (JWT, логин, доступ к ресурсам)
 */
public class AuthException extends ApiException {
    
    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public AuthException(ErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }
}

