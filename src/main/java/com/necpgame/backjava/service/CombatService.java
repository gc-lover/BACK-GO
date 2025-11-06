package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * CombatService - СЃРµСЂРІРёСЃ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ Р±РѕРµРІРѕР№ СЃРёСЃС‚РµРјРѕР№.
 * 
 * РЎРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ РЅР° РѕСЃРЅРѕРІРµ: API-SWAGGER/api/v1/combat/combat.yaml
 */
public interface CombatService {

    /**
     * РќР°С‡Р°С‚СЊ Р±РѕР№.
     */
    CombatState initiateCombat(InitiateCombatRequest request);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ С‚РµРєСѓС‰РµРµ СЃРѕСЃС‚РѕСЏРЅРёРµ Р±РѕСЏ.
     */
    CombatState getCombatState(UUID combatId);

    /**
     * Р’С‹РїРѕР»РЅРёС‚СЊ РґРµР№СЃС‚РІРёРµ РІ Р±РѕСЋ.
     */
    CombatState performCombatAction(UUID combatId, PerformCombatActionRequest request);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РґРѕСЃС‚СѓРїРЅС‹Рµ РґРµР№СЃС‚РІРёСЏ РґР»СЏ РїРµСЂСЃРѕРЅР°Р¶Р°.
     */
    GetAvailableActions200Response getAvailableActions(UUID combatId, UUID characterId);

    /**
     * РЎР±РµР¶Р°С‚СЊ РёР· Р±РѕСЏ.
     */
    FleeCombat200Response fleeCombat(UUID combatId, FleeCombatRequest request);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЂРµР·СѓР»СЊС‚Р°С‚ Р·Р°РІРµСЂС€РµРЅРЅРѕРіРѕ Р±РѕСЏ.
     */
    CombatResult getCombatResult(UUID combatId);
}

