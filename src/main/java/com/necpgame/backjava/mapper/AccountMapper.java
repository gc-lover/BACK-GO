package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.model.Account;
import org.springframework.stereotype.Component;

/**
 * Mapper для преобразования AccountEntity ↔ Account DTO
 */
@Component
public class AccountMapper {
    
    /**
     * Преобразовать Entity в DTO
     */
    public Account toDto(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Account dto = new Account();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastLogin(entity.getLastLogin());
        dto.setIsActive(entity.getIsActive());
        
        return dto;
    }
    
    /**
     * Преобразовать DTO в Entity (для создания нового аккаунта)
     */
    public AccountEntity toEntity(Account dto) {
        if (dto == null) {
            return null;
        }
        
        AccountEntity entity = new AccountEntity();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setLastLogin(dto.getLastLogin());
        entity.setIsActive(dto.getIsActive());
        
        return entity;
    }
}

