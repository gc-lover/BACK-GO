package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.ProgressionApi;
import com.necpgame.backjava.model.AchievementDefinition;
import com.necpgame.backjava.model.GetPlayerAchievements200Response;
import com.necpgame.backjava.model.GetPlayerTitles200Response;
import com.necpgame.backjava.model.ListAchievements200Response;
import com.necpgame.backjava.model.ProgressUpdateResult;
import com.necpgame.backjava.model.SetActiveTitleRequest;
import com.necpgame.backjava.model.UpdateProgressRequest;
import com.necpgame.backjava.service.ProgressionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProgressionController implements ProgressionApi {

    private final ProgressionService progressionService;

    @Override
    public ResponseEntity<ListAchievements200Response> listAchievements(String category, String rarity, Boolean unlocked, Integer page, Integer limit) {
        return ResponseEntity.ok(progressionService.listAchievements(category, rarity, unlocked, page, limit));
    }

    @Override
    public ResponseEntity<AchievementDefinition> getAchievement(UUID achievementId) {
        return ResponseEntity.ok(progressionService.getAchievement(achievementId));
    }

    @Override
    public ResponseEntity<GetPlayerAchievements200Response> getPlayerAchievements(UUID playerId, String status, Integer page, Integer limit) {
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
}

