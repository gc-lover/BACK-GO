package com.necpgame.backjava.exception;

/**
 * Exception для случаев отсутствия прав доступа (403)
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
}

