package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.CombatApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.CombatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ Р±РѕРµРІРѕР№ СЃРёСЃС‚РµРјРѕР№.
 * 
 * Р РµР°Р»РёР·СѓРµС‚ РєРѕРЅС‚СЂР°РєС‚ {@link CombatApi}, СЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРЅС‹Р№ РёР· OpenAPI СЃРїРµС†РёС„РёРєР°С†РёРё.
 * РСЃС‚РѕС‡РЅРёРє: API-SWAGGER/api/v1/combat/combat.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CombatController implements CombatApi {
    
    private final CombatService service;
    
    @Override
    public ResponseEntity<CombatState> initiateCombat(InitiateCombatRequest initiateCombatRequest) {
        log.info("POST /combat/initiate");
        return ResponseEntity.ok(service.initiateCombat(initiateCombatRequest));
    }
    
    @Override
    public ResponseEntity<CombatState> getCombatState(UUID combatId) {
        log.info("GET /combat/{}", combatId);
        return ResponseEntity.ok(service.getCombatState(combatId));
    }
    
    @Override
    public ResponseEntity<CombatState> performCombatAction(UUID combatId, PerformCombatActionRequest performCombatActionRequest) {
        log.info("POST /combat/{}/action", combatId);
        return ResponseEntity.ok(service.performCombatAction(combatId, performCombatActionRequest));
    }
    
    @Override
    public ResponseEntity<GetAvailableActions200Response> getAvailableActions(UUID combatId, UUID characterId) {
        log.info("GET /combat/{}/available-actions?characterId={}", combatId, characterId);
        return ResponseEntity.ok(service.getAvailableActions(combatId, characterId));
    }
    
    @Override
    public ResponseEntity<FleeCombat200Response> fleeCombat(UUID combatId, FleeCombatRequest fleeCombatRequest) {
        log.info("POST /combat/{}/flee", combatId);
        return ResponseEntity.ok(service.fleeCombat(combatId, fleeCombatRequest));
    }
    
    @Override
    public ResponseEntity<CombatResult> getCombatResult(UUID combatId) {
        log.info("GET /combat/{}/result", combatId);
        return ResponseEntity.ok(service.getCombatResult(combatId));
    }
}

