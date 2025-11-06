package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CombatLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * CombatLogRepository - репозиторий для работы с логами боя.
 * 
 * Источник: API-SWAGGER/api/v1/combat/combat.yaml
 */
@Repository
public interface CombatLogRepository extends JpaRepository<CombatLogEntity, UUID> {

    /**
     * Найти все логи боевой сессии.
     */
    @Query("SELECT cl FROM CombatLogEntity cl WHERE cl.combatSessionId = :combatSessionId ORDER BY cl.round, cl.actionOrder")
    List<CombatLogEntity> findByCombatSessionIdOrderByRoundAndActionOrder(UUID combatSessionId);

    /**
     * Найти логи конкретного раунда.
     */
    @Query("SELECT cl FROM CombatLogEntity cl WHERE cl.combatSessionId = :combatSessionId AND cl.round = :round ORDER BY cl.actionOrder")
    List<CombatLogEntity> findBySessionAndRound(UUID combatSessionId, Integer round);

    /**
     * Получить последний action order в раунде.
     */
    @Query("SELECT COALESCE(MAX(cl.actionOrder), 0) FROM CombatLogEntity cl WHERE cl.combatSessionId = :combatSessionId AND cl.round = :round")
    Integer getLastActionOrder(UUID combatSessionId, Integer round);
}

