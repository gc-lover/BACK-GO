package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.GameStartApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.GameStartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * GameStartController - REST контроллер для запуска игры.
 * 
 * Реализует API интерфейс GameStartApi.
 * Все Spring MVC аннотации определены в интерфейсе.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class GameStartController implements GameStartApi {

    private final GameStartService gameStartService;

    @Override
    public ResponseEntity<GameStartResponse> startGame(GameStartRequest body) {
        log.info("POST /v1/game/start - Starting game for character: {}", body.getCharacterId());
        GameStartResponse response = gameStartService.startGame(body);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WelcomeScreenResponse> getWelcomeScreen(UUID characterId) {
        log.info("GET /v1/game/welcome - Getting welcome screen for character: {}", characterId);
        WelcomeScreenResponse response = gameStartService.getWelcomeScreen(characterId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GameReturnResponse> returnToGame(GameReturnRequest body) {
        log.info("POST /v1/game/return - Returning to game for character: {}", body.getCharacterId());
        GameReturnResponse response = gameStartService.returnToGame(body);
        return ResponseEntity.ok(response);
    }
}

