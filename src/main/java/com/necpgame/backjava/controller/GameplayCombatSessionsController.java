package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.GameplayCombatSessionsApi;
import com.necpgame.backjava.model.CombatEndResult;
import com.necpgame.backjava.model.CombatSession;
import com.necpgame.backjava.model.CreateCombatSessionRequest;
import com.necpgame.backjava.model.DamageRequest;
import com.necpgame.backjava.model.DamageResult;
import com.necpgame.backjava.model.EndCombatSessionRequest;
import com.necpgame.backjava.model.GetCombatEvents200Response;
import com.necpgame.backjava.model.NextTurn200Response;
import com.necpgame.backjava.model.UpdateParticipantStatusRequest;
import com.necpgame.backjava.service.GameplayService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GameplayCombatSessionsController implements GameplayCombatSessionsApi {

    private final GameplayService gameplayService;

    @Override
    public ResponseEntity<CombatSession> createCombatSession(CreateCombatSessionRequest createCombatSessionRequest) {
        log.info("POST /gameplay/combat/sessions");
        CombatSession session = gameplayService.createCombatSession(createCombatSessionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @Override
    public ResponseEntity<CombatSession> getCombatSession(UUID sessionId) {
        log.info("GET /gameplay/combat/sessions/{}", sessionId);
        return ResponseEntity.ok(gameplayService.getCombatSession(sessionId));
    }

    @Override
    public ResponseEntity<DamageResult> applyDamage(UUID sessionId, DamageRequest damageRequest) {
        log.info("POST /gameplay/combat/sessions/{}/damage", sessionId);
        return ResponseEntity.ok(gameplayService.applyDamage(sessionId, damageRequest));
    }

    @Override
    public ResponseEntity<NextTurn200Response> nextTurn(UUID sessionId) {
        log.info("POST /gameplay/combat/sessions/{}/turn/next", sessionId);
        return ResponseEntity.ok(gameplayService.nextTurn(sessionId));
    }

    @Override
    public ResponseEntity<CombatEndResult> endCombatSession(UUID sessionId, EndCombatSessionRequest endCombatSessionRequest) {
        log.info("POST /gameplay/combat/sessions/{}/end", sessionId);
        return ResponseEntity.ok(gameplayService.endCombatSession(sessionId, endCombatSessionRequest));
    }

    @Override
    public ResponseEntity<GetCombatEvents200Response> getCombatEvents(UUID sessionId, Integer sinceEventId) {
        log.info("GET /gameplay/combat/sessions/{}/events", sessionId);
        return ResponseEntity.ok(gameplayService.getCombatEvents(sessionId, sinceEventId));
    }

    @Override
    public ResponseEntity<Void> updateParticipantStatus(UUID sessionId, String participantId, UpdateParticipantStatusRequest updateParticipantStatusRequest) {
        log.info("PUT /gameplay/combat/sessions/{}/participants/{}/status", sessionId, participantId);
        gameplayService.updateParticipantStatus(sessionId, participantId, updateParticipantStatusRequest);
        return ResponseEntity.ok().build();
    }
}



