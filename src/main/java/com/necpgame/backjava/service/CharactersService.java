package com.necpgame.backjava.service;

import com.necpgame.backjava.model.CreateCharacter201Response;
import com.necpgame.backjava.model.CreateCharacterRequest;
import com.necpgame.backjava.model.DeleteCharacter200Response;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetCharacterClasses200Response;
import com.necpgame.backjava.model.GetCharacterOrigins200Response;
import com.necpgame.backjava.model.ListCharacters200Response;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for CharactersService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface CharactersService {

    /**
     * POST /characters : РЎРѕР·РґР°РЅРёРµ РЅРѕРІРѕРіРѕ РїРµСЂСЃРѕРЅР°Р¶Р°
     * РЎРѕР·РґР°РµС‚ РЅРѕРІРѕРіРѕ РїРµСЂСЃРѕРЅР°Р¶Р° РґР»СЏ С‚РµРєСѓС‰РµРіРѕ Р°РєРєР°СѓРЅС‚Р°. РџСЂРѕРІРµСЂСЏРµС‚ Р»РёРјРёС‚ РїРµСЂСЃРѕРЅР°Р¶РµР№ Рё РґРѕСЃС‚СѓРїРЅРѕСЃС‚СЊ С„СЂР°РєС†РёРё/РіРѕСЂРѕРґР°.
     *
     * @param createCharacterRequest  (required)
     * @return CreateCharacter201Response
     */
    CreateCharacter201Response createCharacter(CreateCharacterRequest createCharacterRequest);

    /**
     * DELETE /characters/{character_id} : РЈРґР°Р»РµРЅРёРµ РїРµСЂСЃРѕРЅР°Р¶Р°
     * РЈРґР°Р»СЏРµС‚ РїРµСЂСЃРѕРЅР°Р¶Р° С‚РµРєСѓС‰РµРіРѕ Р°РєРєР°СѓРЅС‚Р°. РќРµРѕР±СЂР°С‚РёРјРѕРµ СѓРґР°Р»РµРЅРёРµ РІСЃРµС… РґР°РЅРЅС‹С….
     *
     * @param characterId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРµСЂСЃРѕРЅР°Р¶Р° РґР»СЏ СѓРґР°Р»РµРЅРёСЏ (required)
     * @return DeleteCharacter200Response
     */
    DeleteCharacter200Response deleteCharacter(UUID characterId);

    /**
     * GET /characters/classes : РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… РєР»Р°СЃСЃРѕРІ
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РІСЃРµС… РґРѕСЃС‚СѓРїРЅС‹С… РєР»Р°СЃСЃРѕРІ РїРµСЂСЃРѕРЅР°Р¶РµР№ СЃ РѕРїРёСЃР°РЅРёСЏРјРё.
     *
     * @return GetCharacterClasses200Response
     */
    GetCharacterClasses200Response getCharacterClasses();

    /**
     * GET /characters/origins : РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёР№
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РІСЃРµС… РґРѕСЃС‚СѓРїРЅС‹С… РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёР№ СЃ РѕРїРёСЃР°РЅРёСЏРјРё Рё РґРѕСЃС‚СѓРїРЅС‹РјРё С„СЂР°РєС†РёСЏРјРё.
     *
     * @return GetCharacterOrigins200Response
     */
    GetCharacterOrigins200Response getCharacterOrigins();

    /**
     * GET /characters : РЎРїРёСЃРѕРє РїРµСЂСЃРѕРЅР°Р¶РµР№ РёРіСЂРѕРєР°
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РІСЃРµС… РїРµСЂСЃРѕРЅР°Р¶РµР№ С‚РµРєСѓС‰РµРіРѕ Р°РєРєР°СѓРЅС‚Р° СЃ РєСЂР°С‚РєРѕР№ РёРЅС„РѕСЂРјР°С†РёРµР№.
     *
     * @return ListCharacters200Response
     */
    ListCharacters200Response listCharacters();
}

