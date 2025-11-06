package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * QuestsService - сервис для управления квестовой системой.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/quests/quests.yaml
 */
public interface QuestsService {

    /**
     * Получить список доступных квестов.
     */
    GetAvailableQuests200Response getAvailableQuests(UUID characterId, String type);

    /**
     * Получить активные квесты персонажа.
     */
    GetActiveQuests200Response getActiveQuests(UUID characterId);

    /**
     * Получить детали квеста.
     */
    Quest getQuestDetails(String questId, UUID characterId);

    /**
     * Принять квест.
     */
    AcceptQuest200Response acceptQuest(String questId, AcceptQuestRequest request);

    /**
     * Завершить квест.
     */
    CompleteQuest200Response completeQuest(String questId, CompleteQuestRequest request);

    /**
     * Отказаться от квеста.
     */
    AbandonQuest200Response abandonQuest(String questId, AbandonQuestRequest request);

    /**
     * Получить цели квеста.
     */
    GetQuestObjectives200Response getQuestObjectives(String questId, UUID characterId);
}

