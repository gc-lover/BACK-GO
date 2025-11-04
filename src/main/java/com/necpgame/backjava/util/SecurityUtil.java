package com.necpgame.backjava.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Утилита для работы с Security контекстом
 */
public class SecurityUtil {
    
    /**
     * Получить ID текущего аутентифицированного пользователя
     */
    public static UUID getCurrentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        // Временная заглушка - возвращаем тестовый UUID
        // TODO: реализовать извлечение из JWT токена
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}

