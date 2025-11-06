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
 * REST Controller РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ РїРµСЂСЃРѕРЅР°Р¶Р°РјРё
 * Р РµР°Р»РёР·СѓРµС‚ CharactersApi РёРЅС‚РµСЂС„РµР№СЃ - РІСЃРµ Spring MVC Р°РЅРЅРѕС‚Р°С†РёРё РѕРїСЂРµРґРµР»РµРЅС‹ С‚Р°Рј
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CharactersController implements CharactersApi {
    
    private final CharactersService charactersService;
    
    /**
     * GET /characters - РЎРїРёСЃРѕРє РїРµСЂСЃРѕРЅР°Р¶РµР№ РёРіСЂРѕРєР°
     */
    @Override
    public ResponseEntity<ListCharacters200Response> listCharacters() {
        log.info("GET /characters");
        ListCharacters200Response response = charactersService.listCharacters();
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /characters - РЎРѕР·РґР°С‚СЊ РЅРѕРІРѕРіРѕ РїРµСЂСЃРѕРЅР°Р¶Р°
     */
    @Override
    public ResponseEntity<CreateCharacter201Response> createCharacter(CreateCharacterRequest createCharacterRequest) {
        log.info("POST /characters - {}", createCharacterRequest.getName());
        CreateCharacter201Response response = charactersService.createCharacter(createCharacterRequest);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * DELETE /characters/{characterId} - РЈРґР°Р»РёС‚СЊ РїРµСЂСЃРѕРЅР°Р¶Р°
     */
    @Override
    public ResponseEntity<DeleteCharacter200Response> deleteCharacter(UUID characterId) {
        log.info("DELETE /characters/{}", characterId);
        DeleteCharacter200Response response = charactersService.deleteCharacter(characterId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /characters/classes - РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… РєР»Р°СЃСЃРѕРІ РїРµСЂСЃРѕРЅР°Р¶РµР№
     */
    @Override
    public ResponseEntity<GetCharacterClasses200Response> getCharacterClasses() {
        log.info("GET /characters/classes");
        GetCharacterClasses200Response response = charactersService.getCharacterClasses();
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /characters/origins - РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёР№ РїРµСЂСЃРѕРЅР°Р¶РµР№
     */
    @Override
    public ResponseEntity<GetCharacterOrigins200Response> getCharacterOrigins() {
        log.info("GET /characters/origins");
        GetCharacterOrigins200Response response = charactersService.getCharacterOrigins();
        return ResponseEntity.ok(response);
    }
}
