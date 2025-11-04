package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.CharactersApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.CharactersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller для работы с персонажами
 * Реализует сгенерированный CharactersApi интерфейс
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CharactersController implements CharactersApi {
    
    private final CharactersService charactersService;
    
    /**
     * GET /characters - Список персонажей игрока
     */
    @Override
    public ResponseEntity<ListCharacters200Response> listCharacters() {
        log.info("GET /characters");
        ListCharacters200Response response = charactersService.listCharacters();
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /characters - Создать нового персонажа
     */
    @Override
    public ResponseEntity<CreateCharacter201Response> createCharacter(CreateCharacterRequest request) {
        log.info("POST /characters - {}", request.getName());
        CreateCharacter201Response response = charactersService.createCharacter(request);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * GET /characters/{characterId} - Детальная информация о персонаже
     */
    @Override
    public ResponseEntity<Character> getCharacter(UUID characterId) {
        log.info("GET /characters/{}", characterId);
        Character response = charactersService.getCharacter(characterId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /characters/{characterId} - Удалить персонажа
     */
    @Override
    public ResponseEntity<DeleteCharacter200Response> deleteCharacter(UUID characterId) {
        log.info("DELETE /characters/{}", characterId);
        DeleteCharacter200Response response = charactersService.deleteCharacter(characterId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /characters/classes - Список доступных классов
     */
    @Override
    public ResponseEntity<GetCharacterClasses200Response> getCharacterClasses() {
        log.info("GET /characters/classes");
        GetCharacterClasses200Response response = charactersService.getCharacterClasses();
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /characters/origins - Список доступных происхождений
     */
    @Override
    public ResponseEntity<GetCharacterOrigins200Response> getCharacterOrigins() {
        log.info("GET /characters/origins");
        GetCharacterOrigins200Response response = charactersService.getCharacterOrigins();
        return ResponseEntity.ok(response);
    }
}

