package com.necpgame.backjava.controller;

import com.necpgame.backjava.model.GetCities200Response;
import com.necpgame.backjava.service.LocationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller для работы с локациями (городами)
 */
@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationsController {
    
    private final LocationsService locationsService;
    
    /**
     * GET /locations/cities - Список доступных городов
     */
    @GetMapping("/cities")
    public ResponseEntity<GetCities200Response> getCities(
            @RequestParam(required = false) UUID factionId,
            @RequestParam(required = false) String region) {
        log.info("GET /locations/cities?factionId={}&region={}", factionId, region);
        GetCities200Response response = locationsService.getCities(factionId, region);
        return ResponseEntity.ok(response);
    }
}

