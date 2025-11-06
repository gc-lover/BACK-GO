package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.CharactersStatusApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.CharactersStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller для работы со статусом и характеристиками персонажа.
 * 
 * Реализует контракт {@link CharactersStatusApi}, сгенерированный из OpenAPI спецификации.
 * Источник: API-SWAGGER/api/v1/characters/status.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CharactersStatusController implements CharactersStatusApi {
    
    private final CharactersStatusService service;
    
    @Override
    public ResponseEntity<CharacterStatus> getCharacterStatus(UUID characterId) {
        log.info("GET /characters/{}/status", characterId);
        return ResponseEntity.ok(service.getCharacterStatus(characterId));
    }
    
    @Override
    public ResponseEntity<CharacterStats> getCharacterStats(UUID characterId) {
        log.info("GET /characters/{}/stats", characterId);
        return ResponseEntity.ok(service.getCharacterStats(characterId));
    }
    
    @Override
    public ResponseEntity<GetCharacterSkills200Response> getCharacterSkills(UUID characterId) {
        log.info("GET /characters/{}/skills", characterId);
        return ResponseEntity.ok(service.getCharacterSkills(characterId));
    }
    
    @Override
    public ResponseEntity<CharacterStatus> updateCharacterStatus(UUID characterId, UpdateCharacterStatusRequest updateCharacterStatusRequest) {
        log.info("POST /characters/{}/status/update", characterId);
        return ResponseEntity.ok(service.updateCharacterStatus(characterId, updateCharacterStatusRequest));
    }
}

