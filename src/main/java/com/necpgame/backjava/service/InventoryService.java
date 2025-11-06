package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * InventoryService - сервис для управления инвентарем.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
public interface InventoryService {

    /**
     * Получить инвентарь персонажа.
     */
    InventoryResponse getInventory(UUID characterId, ItemCategory category);

    /**
     * Получить экипировку персонажа.
     */
    GetEquipment200Response getEquipment(UUID characterId);

    /**
     * Экипировать предмет.
     */
    EquipItem200Response equipItem(EquipRequest request);

    /**
     * Снять предмет.
     */
    UnequipItem200Response unequipItem(UnequipItemRequest request);

    /**
     * Использовать предмет.
     */
    UseItem200Response useItem(UseItemRequest request);

    /**
     * Выбросить предмет.
     */
    DropItem200Response dropItem(UUID characterId, String itemId);
}

