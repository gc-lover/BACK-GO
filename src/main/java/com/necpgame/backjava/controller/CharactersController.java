package com.necpgame.backjava.controller;

import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.CharactersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller для работы с персонажами
 */
@Slf4j
@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
public class CharactersController {
    
    private final CharactersService charactersService;
    
    /**
     * GET /characters - Список персонажей игрока
     */
    @GetMapping
    public ResponseEntity<ListCharacters200Response> listCharacters() {
        log.info("GET /characters");
        ListCharacters200Response response = charactersService.listCharacters();
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /characters - Создать нового персонажа
     */
    @PostMapping
    public ResponseEntity<CreateCharacter201Response> createCharacter(@Valid @RequestBody CreateCharacterRequest request) {
        log.info("POST /characters - {}", request.getName());
        CreateCharacter201Response response = charactersService.createCharacter(request);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * GET /characters/{characterId} - Детальная информация о персонаже
     * TODO: Add this endpoint to OpenAPI spec
     */
    @GetMapping("/{characterId}")
    public ResponseEntity<GameCharacter> getCharacterById(@PathVariable UUID characterId) {
        log.info("GET /characters/{}", characterId);
        // TODO: Временно закомментирован, пока не добавим в OpenAPI spec
        // GameCharacter response = charactersService.getCharacter(characterId);
        // return ResponseEntity.ok(response);
        throw new UnsupportedOperationException("Endpoint not yet implemented");
    }
    
    /**
     * DELETE /characters/{characterId} - Удалить персонажа
     */
    @DeleteMapping("/{characterId}")
    public ResponseEntity<DeleteCharacter200Response> deleteCharacter(@PathVariable UUID characterId) {
        log.info("DELETE /characters/{}", characterId);
        DeleteCharacter200Response response = charactersService.deleteCharacter(characterId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /characters/classes - Список доступных классов персонажей
     */
    @GetMapping("/classes")
    public ResponseEntity<GetCharacterClasses200Response> getCharacterClasses() {
        log.info("GET /characters/classes");
        GetCharacterClasses200Response response = charactersService.getCharacterClasses();
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /characters/origins - Список доступных происхождений персонажей
     */
    @GetMapping("/origins")
    public ResponseEntity<GetCharacterOrigins200Response> getCharacterOrigins() {
        log.info("GET /characters/origins");
        GetCharacterOrigins200Response response = charactersService.getCharacterOrigins();
        return ResponseEntity.ok(response);
    }
}
