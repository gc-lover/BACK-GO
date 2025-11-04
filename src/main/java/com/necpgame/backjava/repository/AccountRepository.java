package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с аккаунтами игроков
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    
    /**
     * Найти аккаунт по email
     */
    Optional<AccountEntity> findByEmail(String email);
    
    /**
     * Найти аккаунт по username
     */
    Optional<AccountEntity> findByUsername(String username);
    
    /**
     * Проверить существование аккаунта по email
     */
    boolean existsByEmail(String email);
    
    /**
     * Проверить существование аккаунта по username
     */
    boolean existsByUsername(String username);
    
    /**
     * Найти активный аккаунт по email или username
     */
    @Query("SELECT a FROM AccountEntity a WHERE (a.email = :login OR a.username = :login) AND a.isActive = true")
    Optional<AccountEntity> findActiveByEmailOrUsername(@Param("login") String login);
}

