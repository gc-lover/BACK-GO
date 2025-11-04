package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.LocationsApi;
import com.necpgame.backjava.model.GetCities200Response;
import com.necpgame.backjava.service.LocationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller для работы с локациями (городами)
 * Реализует сгенерированный LocationsApi интерфейс
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LocationsController implements LocationsApi {
    
    private final LocationsService locationsService;
    
    /**
     * GET /locations/cities - Список доступных городов
     */
    @Override
    public ResponseEntity<GetCities200Response> getCities(UUID factionId, String region) {
        log.info("GET /locations/cities?factionId={}&region={}", factionId, region);
        GetCities200Response response = locationsService.getCities(factionId, region);
        return ResponseEntity.ok(response);
    }
}

