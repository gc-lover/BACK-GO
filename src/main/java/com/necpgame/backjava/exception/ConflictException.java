package com.necpgame.backjava.exception;

/**
 * Exception для случаев конфликта данных (409)
 * Например, email или username уже существует
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
}

