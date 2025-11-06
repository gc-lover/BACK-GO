package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * InventoryService - СЃРµСЂРІРёСЃ РґР»СЏ СѓРїСЂР°РІР»РµРЅРёСЏ РёРЅРІРµРЅС‚Р°СЂРµРј.
 * 
 * РЎРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ РёР·: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
public interface InventoryService {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РёРЅРІРµРЅС‚Р°СЂСЊ РїРµСЂСЃРѕРЅР°Р¶Р°.
     */
    InventoryResponse getInventory(UUID characterId, ItemCategory category);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЌРєРёРїРёСЂРѕРІРєСѓ РїРµСЂСЃРѕРЅР°Р¶Р°.
     */
    GetEquipment200Response getEquipment(UUID characterId);

    /**
     * Р­РєРёРїРёСЂРѕРІР°С‚СЊ РїСЂРµРґРјРµС‚.
     */
    EquipItem200Response equipItem(EquipRequest request);

    /**
     * РЎРЅСЏС‚СЊ РїСЂРµРґРјРµС‚.
     */
    UnequipItem200Response unequipItem(UnequipItemRequest request);

    /**
     * РСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РїСЂРµРґРјРµС‚.
     */
    UseItem200Response useItem(UseItemRequest request);

    /**
     * Р’С‹Р±СЂРѕСЃРёС‚СЊ РїСЂРµРґРјРµС‚.
     */
    DropItem200Response dropItem(UUID characterId, String itemId, Integer quantity);
}

