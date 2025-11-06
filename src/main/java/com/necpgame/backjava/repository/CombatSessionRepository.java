package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CombatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CombatSessionRepository - репозиторий для работы с боевыми сессиями.
 * 
 * Источник: API-SWAGGER/api/v1/combat/combat.yaml
 */
@Repository
public interface CombatSessionRepository extends JpaRepository<CombatSessionEntity, UUID> {

    /**
     * Найти активную боевую сессию персонажа.
     */
    @Query("SELECT cs FROM CombatSessionEntity cs WHERE cs.characterId = :characterId AND cs.status = 'ACTIVE'")
    Optional<CombatSessionEntity> findActiveByCharacterId(UUID characterId);

    /**
     * Проверить есть ли активный бой у персонажа.
     */
    @Query("SELECT COUNT(cs) > 0 FROM CombatSessionEntity cs WHERE cs.characterId = :characterId AND cs.status = 'ACTIVE'")
    boolean hasActiveCombat(UUID characterId);

    /**
     * Найти все бои персонажа.
     */
    @Query("SELECT cs FROM CombatSessionEntity cs WHERE cs.characterId = :characterId ORDER BY cs.createdAt DESC")
    List<CombatSessionEntity> findByCharacterIdOrderByCreatedAtDesc(UUID characterId);

    /**
     * Найти завершенные бои персонажа.
     */
    @Query("SELECT cs FROM CombatSessionEntity cs WHERE cs.characterId = :characterId AND cs.status IN ('ENDED', 'FLED') ORDER BY cs.endedAt DESC")
    List<CombatSessionEntity> findCompletedByCharacterId(UUID characterId);
}

