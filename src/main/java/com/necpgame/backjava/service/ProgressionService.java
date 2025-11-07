package com.necpgame.backjava.service;

import com.necpgame.backjava.model.AchievementDefinition;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetPlayerAchievements200Response;
import com.necpgame.backjava.model.GetPlayerTitles200Response;
import com.necpgame.backjava.model.ListAchievements200Response;
import org.springframework.lang.Nullable;
import com.necpgame.backjava.model.ProgressUpdateResult;
import com.necpgame.backjava.model.SetActiveTitleRequest;
import java.util.UUID;
import com.necpgame.backjava.model.UpdateProgressRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for ProgressionService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface ProgressionService {

    /**
     * GET /progression/achievements/{achievement_id} : Получить детали достижения
     *
     * @param achievementId  (required)
     * @return AchievementDefinition
     */
    AchievementDefinition getAchievement(UUID achievementId);

    /**
     * GET /progression/achievements/player/{player_id} : Получить достижения игрока
     *
     * @param playerId  (required)
     * @param status  (optional)
     * @param page Номер страницы (начинается с 1) (optional, default to 1)
     * @param pageSize Количество элементов на странице (optional, default to 20)
     * @return GetPlayerAchievements200Response
     */
    GetPlayerAchievements200Response getPlayerAchievements(UUID playerId, String status, Integer page, Integer pageSize);

    /**
     * GET /progression/achievements/player/{player_id}/titles : Получить разблокированные титулы игрока
     *
     * @param playerId  (required)
     * @return GetPlayerTitles200Response
     */
    GetPlayerTitles200Response getPlayerTitles(UUID playerId);

    /**
     * GET /progression/achievements : Получить список всех достижений
     *
     * @param category  (optional)
     * @param rarity  (optional)
     * @param unlocked Фильтр по статусу разблокировки (optional)
     * @param page Номер страницы (начинается с 1) (optional, default to 1)
     * @param pageSize Количество элементов на странице (optional, default to 20)
     * @return ListAchievements200Response
     */
    ListAchievements200Response listAchievements(String category, String rarity, Boolean unlocked, Integer page, Integer pageSize);

    /**
     * PUT /progression/achievements/player/{player_id}/titles/active : Установить активный титул
     *
     * @param playerId  (required)
     * @param setActiveTitleRequest  (required)
     * @return Void
     */
    Void setActiveTitle(UUID playerId, SetActiveTitleRequest setActiveTitleRequest);

    /**
     * POST /progression/achievements/player/{player_id}/progress : Обновить прогресс достижения
     * Используется backend системами для автоматического обновления прогресса
     *
     * @param playerId  (required)
     * @param updateProgressRequest  (required)
     * @return ProgressUpdateResult
     */
    ProgressUpdateResult updateAchievementProgress(UUID playerId, UpdateProgressRequest updateProgressRequest);
}

