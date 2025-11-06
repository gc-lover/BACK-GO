package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * LocationsService - сервис для работы с игровыми локациями.
 * 
 * Сгенерировано на основе: API-SWAGGER/api/v1/locations/locations.yaml
 */
public interface LocationsService {

    /**
     * Получить список всех доступных локаций.
     */
    GetLocations200Response getLocations(UUID characterId, String region, String dangerLevel, Integer minLevel);

    /**
     * Получить детальную информацию о локации.
     */
    LocationDetails getLocationDetails(String locationId, UUID characterId);

    /**
     * Получить текущую локацию персонажа.
     */
    LocationDetails getCurrentLocation(UUID characterId);

    /**
     * Переместиться в другую локацию.
     */
    TravelResponse travelToLocation(TravelRequest request);

    /**
     * Получить доступные действия в локации.
     */
    GetLocationActions200Response getLocationActions(String locationId, UUID characterId);

    /**
     * Получить список соседних локаций.
     */
    GetConnectedLocations200Response getConnectedLocations(String locationId, UUID characterId);
}
