package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.ProgressionApi;
import com.necpgame.backjava.model.AchievementDefinition;
import com.necpgame.backjava.model.GetPlayerAchievements200Response;
import com.necpgame.backjava.model.GetPlayerTitles200Response;
import com.necpgame.backjava.model.GetSeasonalLeaderboard200Response;
import com.necpgame.backjava.model.GuildLeaderboardResponse;
import com.necpgame.backjava.model.GuildRankResponse;
import com.necpgame.backjava.model.LeaderboardResponse;
import com.necpgame.backjava.model.ListAchievements200Response;
import com.necpgame.backjava.model.PlayerRankResponse;
import com.necpgame.backjava.model.ProgressUpdateResult;
import com.necpgame.backjava.model.SetActiveTitleRequest;
import com.necpgame.backjava.model.UpdateLeaderboardScore200Response;
import com.necpgame.backjava.model.UpdateProgressRequest;
import com.necpgame.backjava.model.UpdateScoreRequest;
import com.necpgame.backjava.service.ProgressionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProgressionController implements ProgressionApi {

    private final ProgressionService progressionService;

    @Override
    public ResponseEntity<ListAchievements200Response> listAchievements(@Nullable String category, @Nullable String rarity, @Nullable Boolean unlocked, @Nullable Integer page, @Nullable Integer limit) {
        return ResponseEntity.ok(progressionService.listAchievements(category, rarity, unlocked, page, limit));
    }

    @Override
    public ResponseEntity<AchievementDefinition> getAchievement(UUID achievementId) {
        return ResponseEntity.ok(progressionService.getAchievement(achievementId));
    }

    @Override
    public ResponseEntity<GetPlayerAchievements200Response> getPlayerAchievements(UUID playerId, @Nullable String status, @Nullable Integer page, @Nullable Integer limit) {
        return ResponseEntity.ok(progressionService.getPlayerAchievements(playerId, status, page, limit));
    }

    @Override
    public ResponseEntity<GetPlayerTitles200Response> getPlayerTitles(UUID playerId) {
        return ResponseEntity.ok(progressionService.getPlayerTitles(playerId));
    }

    @Override
    public ResponseEntity<Void> setActiveTitle(UUID playerId, SetActiveTitleRequest setActiveTitleRequest) {
        progressionService.setActiveTitle(playerId, setActiveTitleRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ProgressUpdateResult> updateAchievementProgress(UUID playerId, UpdateProgressRequest updateProgressRequest) {
        return ResponseEntity.ok(progressionService.updateAchievementProgress(playerId, updateProgressRequest));
    }

    @Override
    public ResponseEntity<LeaderboardResponse> getFriendLeaderboard(UUID playerId, String category) {
        return ResponseEntity.ok(progressionService.getFriendLeaderboard(playerId, category));
    }

    @Override
    public ResponseEntity<LeaderboardResponse> getGlobalLeaderboard(String category, @Nullable Integer top, @Nullable Integer offset) {
        return ResponseEntity.ok(progressionService.getGlobalLeaderboard(category, top, offset));
    }

    @Override
    public ResponseEntity<GuildLeaderboardResponse> getGuildLeaderboard(String category, @Nullable Integer top) {
        return ResponseEntity.ok(progressionService.getGuildLeaderboard(category, top));
    }

    @Override
    public ResponseEntity<GuildRankResponse> getGuildRank(String category, UUID guildId) {
        return ResponseEntity.ok(progressionService.getGuildRank(category, guildId));
    }

    @Override
    public ResponseEntity<PlayerRankResponse> getPlayerGlobalRank(String category, UUID playerId) {
        return ResponseEntity.ok(progressionService.getPlayerGlobalRank(category, playerId));
    }

    @Override
    public ResponseEntity<GetSeasonalLeaderboard200Response> getSeasonalLeaderboard(String seasonId, String category, @Nullable Integer top) {
        return ResponseEntity.ok(progressionService.getSeasonalLeaderboard(seasonId, category, top));
    }

    @Override
    public ResponseEntity<UpdateLeaderboardScore200Response> updateLeaderboardScore(UpdateScoreRequest updateScoreRequest) {
        return ResponseEntity.ok(progressionService.updateLeaderboardScore(updateScoreRequest));
    }
}

