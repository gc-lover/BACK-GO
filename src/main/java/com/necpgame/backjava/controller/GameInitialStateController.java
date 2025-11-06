package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.GameInitialStateApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.GameInitialStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * GameInitialStateController - REST контроллер для начального состояния игры.
 * 
 * Реализует API интерфейс GameInitialStateApi.
 * Все Spring MVC аннотации определены в интерфейсе.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class GameInitialStateController implements GameInitialStateApi {

    private final GameInitialStateService gameInitialStateService;

    @Override
    public ResponseEntity<InitialStateResponse> getInitialState(UUID characterId) {
        log.info("GET /v1/game/initial-state - Getting initial state for character: {}", characterId);
        InitialStateResponse response = gameInitialStateService.getInitialState(characterId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TutorialStepsResponse> getTutorialSteps(UUID characterId) {
        log.info("GET /v1/game/tutorial-steps - Getting tutorial steps for character: {}", characterId);
        TutorialStepsResponse response = gameInitialStateService.getTutorialSteps(characterId);
        return ResponseEntity.ok(response);
    }
}

