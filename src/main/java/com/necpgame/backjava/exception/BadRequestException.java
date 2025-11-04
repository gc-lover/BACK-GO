package com.necpgame.backjava.exception;

/**
 * Exception для случаев некорректных данных запроса (400)
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}

