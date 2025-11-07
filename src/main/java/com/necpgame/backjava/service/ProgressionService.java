package com.necpgame.backjava.service;

import com.necpgame.backjava.model.AchievementDefinition;
import com.necpgame.backjava.model.GetPlayerAchievements200Response;
import com.necpgame.backjava.model.GetPlayerTitles200Response;
import com.necpgame.backjava.model.GetSeasonalLeaderboard200Response;
import com.necpgame.backjava.model.ListAchievements200Response;
import com.necpgame.backjava.model.LeaderboardResponse;
import com.necpgame.backjava.model.GuildLeaderboardResponse;
import com.necpgame.backjava.model.GuildRankResponse;
import com.necpgame.backjava.model.PlayerRankResponse;
import org.springframework.lang.Nullable;
import com.necpgame.backjava.model.ProgressUpdateResult;
import com.necpgame.backjava.model.SetActiveTitleRequest;
import java.util.UUID;
import com.necpgame.backjava.model.UpdateProgressRequest;
import com.necpgame.backjava.model.UpdateLeaderboardScore200Response;
import com.necpgame.backjava.model.UpdateScoreRequest;
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

    /**
     * GET /progression/leaderboards/friends/{player_id}/{category} : Получить рейтинг среди друзей
     *
     * @param playerId  (required)
     * @param category  (required)
     * @return LeaderboardResponse
     */
    LeaderboardResponse getFriendLeaderboard(UUID playerId, String category);

    /**
     * GET /progression/leaderboards/global/{category} : Получить глобальный рейтинг
     *
     * @param category  (required)
     * @param top Количество топ записей (optional, default to 100)
     * @param offset  (optional, default to 0)
     * @return LeaderboardResponse
     */
    LeaderboardResponse getGlobalLeaderboard(String category, Integer top, Integer offset);

    /**
     * GET /progression/leaderboards/guilds/{category} : Получить рейтинг гильдий
     *
     * @param category  (required)
     * @param top  (optional, default to 100)
     * @return GuildLeaderboardResponse
     */
    GuildLeaderboardResponse getGuildLeaderboard(String category, Integer top);

    /**
     * GET /progression/leaderboards/guilds/{category}/guild/{guild_id} : Получить позицию гильдии в рейтинге
     *
     * @param category  (required)
     * @param guildId  (required)
     * @return GuildRankResponse
     */
    GuildRankResponse getGuildRank(String category, UUID guildId);

    /**
     * GET /progression/leaderboards/global/{category}/player/{player_id} : Получить позицию игрока в глобальном рейтинге
     *
     * @param category  (required)
     * @param playerId  (required)
     * @return PlayerRankResponse
     */
    PlayerRankResponse getPlayerGlobalRank(String category, UUID playerId);

    /**
     * GET /progression/leaderboards/seasonal/{season_id}/{category} : Получить сезонный рейтинг
     *
     * @param seasonId ID сезона (или "current" для текущего) (required)
     * @param category  (required)
     * @param top  (optional, default to 100)
     * @return GetSeasonalLeaderboard200Response
     */
    GetSeasonalLeaderboard200Response getSeasonalLeaderboard(String seasonId, String category, Integer top);

    /**
     * POST /progression/leaderboards/update : Обновить значение в рейтинге
     * Используется backend системами для обновления рейтингов
     *
     * @param updateScoreRequest  (required)
     * @return UpdateLeaderboardScore200Response
     */
    UpdateLeaderboardScore200Response updateLeaderboardScore(UpdateScoreRequest updateScoreRequest);
}

