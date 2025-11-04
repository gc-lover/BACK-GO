package com.necpgame.backjava.exception;

/**
 * Exception для случаев ошибки аутентификации (401)
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}

