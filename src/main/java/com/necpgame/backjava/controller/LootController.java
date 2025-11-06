package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.LootApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.LootService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller для системы лута
 * Implements LootApi - сгенерирован из loot-system.yaml
 */
@RestController
@RequiredArgsConstructor
public class LootController implements LootApi {

    private final LootService lootService;

    @Override
    public ResponseEntity<GeneratedLoot> generateLoot(GenerateLootRequest generateLootRequest) {
        return ResponseEntity.ok(lootService.generateLoot(generateLootRequest));
    }

    @Override
    public ResponseEntity<LootDrop> getLootDrop(String dropId) {
        return ResponseEntity.ok(lootService.getLootDrop(dropId));
    }

    @Override
    public ResponseEntity<GetRollResult200Response> getRollResult(String rollId) {
        return ResponseEntity.ok(lootService.getRollResult(rollId));
    }

    @Override
    public ResponseEntity<LootItem200Response> lootItem(String dropId, LootItemRequest lootItemRequest) {
        return ResponseEntity.ok(lootService.lootItem(dropId, lootItemRequest));
    }
}

