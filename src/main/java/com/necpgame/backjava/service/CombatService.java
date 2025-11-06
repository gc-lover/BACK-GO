package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * CombatService - сервис для работы с боевой системой.
 * 
 * Сгенерировано на основе: API-SWAGGER/api/v1/combat/combat.yaml
 */
public interface CombatService {

    /**
     * Начать бой.
     */
    CombatState initiateCombat(InitiateCombatRequest request);

    /**
     * Получить текущее состояние боя.
     */
    CombatState getCombatState(UUID combatId);

    /**
     * Выполнить действие в бою.
     */
    CombatState performCombatAction(UUID combatId, PerformCombatActionRequest request);

    /**
     * Получить доступные действия для персонажа.
     */
    GetAvailableActions200Response getAvailableActions(UUID combatId, UUID characterId);

    /**
     * Сбежать из боя.
     */
    FleeCombat200Response fleeCombat(UUID combatId, FleeCombatRequest request);

    /**
     * Получить результат завершенного боя.
     */
    CombatResult getCombatResult(UUID combatId);
}

