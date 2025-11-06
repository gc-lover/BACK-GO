package com.necpgame.backjava.service;

import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetFactions200Response;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for FactionsService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface FactionsService {

    /**
     * GET /factions : РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… С„СЂР°РєС†РёР№
     * РџРѕР»СѓС‡Р°РµС‚ СЃРїРёСЃРѕРє РІСЃРµС… РґРѕСЃС‚СѓРїРЅС‹С… С„СЂР°РєС†РёР№. РњРѕР¶РµС‚ Р±С‹С‚СЊ РѕС‚С„РёР»СЊС‚СЂРѕРІР°РЅ РїРѕ РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёСЋ.
     *
     * @param origin Р¤РёР»СЊС‚СЂ РїРѕ РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёСЋ (РѕРїС†РёРѕРЅР°Р»СЊРЅРѕ) (optional)
     * @return GetFactions200Response
     */
    GetFactions200Response getFactions(String origin);
}

