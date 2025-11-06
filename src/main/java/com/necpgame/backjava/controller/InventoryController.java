package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.InventoryInventoryApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller для управления инвентарем.
 * 
 * Реализует контракт {@link InventoryInventoryApi}, сгенерированный из OpenAPI.
 * Источник: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InventoryController implements InventoryInventoryApi {
    
    private final InventoryService service;
    
    @Override
    public ResponseEntity<InventoryResponse> getInventory(UUID characterId, ItemCategory category) {
        log.info("GET /inventory?characterId={}&category={}", characterId, category);
        return ResponseEntity.ok(service.getInventory(characterId, category));
    }
    
    @Override
    public ResponseEntity<GetEquipment200Response> getEquipment(UUID characterId) {
        log.info("GET /inventory/equipment?characterId={}", characterId);
        return ResponseEntity.ok(service.getEquipment(characterId));
    }
    
    @Override
    public ResponseEntity<EquipItem200Response> equipItem(EquipRequest equipRequest) {
        log.info("POST /inventory/equipment");
        return ResponseEntity.ok(service.equipItem(equipRequest));
    }
    
    @Override
    public ResponseEntity<UnequipItem200Response> unequipItem(UnequipItemRequest unequipItemRequest) {
        log.info("DELETE /inventory/equipment");
        return ResponseEntity.ok(service.unequipItem(unequipItemRequest));
    }
    
    @Override
    public ResponseEntity<UseItem200Response> useItem(UseItemRequest useItemRequest) {
        log.info("POST /inventory/use");
        return ResponseEntity.ok(service.useItem(useItemRequest));
    }
    
    @Override
    public ResponseEntity<DropItem200Response> dropItem(UUID characterId, String itemId) {
        log.info("DELETE /inventory/drop?characterId={}&itemId={}", characterId, itemId);
        return ResponseEntity.ok(service.dropItem(characterId, itemId));
    }
}

