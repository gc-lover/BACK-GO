package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.GameSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * GameSessionRepository - репозиторий для работы с игровыми сессиями.
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSessionEntity, UUID> {

    /**
     * Найти активную сессию для персонажа.
     *
     * @param characterId ID персонажа
     * @return активная сессия
     */
    Optional<GameSessionEntity> findByCharacterIdAndIsActiveTrue(UUID characterId);

    /**
     * Найти все сессии персонажа.
     *
     * @param characterId ID персонажа
     * @return список сессий
     */
    List<GameSessionEntity> findByCharacterIdOrderByCreatedAtDesc(UUID characterId);

    /**
     * Найти все активные сессии аккаунта.
     *
     * @param accountId ID аккаунта
     * @return список активных сессий
     */
    List<GameSessionEntity> findByAccountIdAndIsActiveTrue(UUID accountId);

    /**
     * Деактивировать все сессии персонажа.
     *
     * @param characterId ID персонажа
     */
    @Query("UPDATE GameSessionEntity s SET s.isActive = false WHERE s.characterId = :characterId AND s.isActive = true")
    void deactivateAllSessionsByCharacterId(@Param("characterId") UUID characterId);
}

