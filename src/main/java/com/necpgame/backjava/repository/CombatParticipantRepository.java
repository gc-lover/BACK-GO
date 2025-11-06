package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CombatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CombatParticipantRepository - репозиторий для работы с участниками боя.
 * 
 * Источник: API-SWAGGER/api/v1/combat/combat.yaml
 */
@Repository
public interface CombatParticipantRepository extends JpaRepository<CombatParticipantEntity, UUID> {

    /**
     * Найти всех участников боевой сессии.
     */
    @Query("SELECT cp FROM CombatParticipantEntity cp WHERE cp.combatSessionId = :combatSessionId ORDER BY cp.initiative DESC")
    List<CombatParticipantEntity> findByCombatSessionIdOrderByInitiativeDesc(UUID combatSessionId);

    /**
     * Найти живых участников боя.
     */
    @Query("SELECT cp FROM CombatParticipantEntity cp WHERE cp.combatSessionId = :combatSessionId AND cp.isAlive = true ORDER BY cp.initiative DESC")
    List<CombatParticipantEntity> findAliveByCombatSessionId(UUID combatSessionId);

    /**
     * Найти участника по ID (participantId).
     */
    @Query("SELECT cp FROM CombatParticipantEntity cp WHERE cp.combatSessionId = :combatSessionId AND cp.participantId = :participantId")
    Optional<CombatParticipantEntity> findBySessionAndParticipantId(UUID combatSessionId, String participantId);

    /**
     * Посчитать живых врагов в бою.
     */
    @Query("SELECT COUNT(cp) FROM CombatParticipantEntity cp WHERE cp.combatSessionId = :combatSessionId AND cp.participantType = 'ENEMY' AND cp.isAlive = true")
    long countAliveEnemies(UUID combatSessionId);

    /**
     * Посчитать живых союзников (игрок + NPC).
     */
    @Query("SELECT COUNT(cp) FROM CombatParticipantEntity cp WHERE cp.combatSessionId = :combatSessionId AND cp.participantType IN ('PLAYER', 'NPC') AND cp.isAlive = true")
    long countAliveAllies(UUID combatSessionId);
}

