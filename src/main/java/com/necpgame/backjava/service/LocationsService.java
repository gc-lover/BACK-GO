package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * LocationsService - СЃРµСЂРІРёСЃ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ РёРіСЂРѕРІС‹РјРё Р»РѕРєР°С†РёСЏРјРё.
 * 
 * РЎРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ РЅР° РѕСЃРЅРѕРІРµ: API-SWAGGER/api/v1/locations/locations.yaml
 */
public interface LocationsService {

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє РІСЃРµС… РґРѕСЃС‚СѓРїРЅС‹С… Р»РѕРєР°С†РёР№.
     */
    GetLocations200Response getLocations(UUID characterId, String region, String dangerLevel, Integer minLevel);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РґРµС‚Р°Р»СЊРЅСѓСЋ РёРЅС„РѕСЂРјР°С†РёСЋ Рѕ Р»РѕРєР°С†РёРё.
     */
    LocationDetails getLocationDetails(String locationId, UUID characterId);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ С‚РµРєСѓС‰СѓСЋ Р»РѕРєР°С†РёСЋ РїРµСЂСЃРѕРЅР°Р¶Р°.
     */
    LocationDetails getCurrentLocation(UUID characterId);

    /**
     * РџРµСЂРµРјРµСЃС‚РёС‚СЊСЃСЏ РІ РґСЂСѓРіСѓСЋ Р»РѕРєР°С†РёСЋ.
     */
    TravelResponse travelToLocation(TravelRequest request);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ РґРѕСЃС‚СѓРїРЅС‹Рµ РґРµР№СЃС‚РІРёСЏ РІ Р»РѕРєР°С†РёРё.
     */
    GetLocationActions200Response getLocationActions(String locationId, UUID characterId);

    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє СЃРѕСЃРµРґРЅРёС… Р»РѕРєР°С†РёР№.
     */
    GetConnectedLocations200Response getConnectedLocations(String locationId, UUID characterId);
}
