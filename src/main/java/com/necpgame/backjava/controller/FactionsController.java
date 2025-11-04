package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.FactionsApi;
import com.necpgame.backjava.model.GetFactions200Response;
import com.necpgame.backjava.service.FactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller для работы с фракциями
 * Реализует сгенерированный FactionsApi интерфейс из OpenAPI спецификации
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FactionsController implements FactionsApi {
    
    private final FactionsService factionsService;
    
    /**
     * GET /factions - Список доступных фракций
     * OpenAPI спецификация определяет все аннотации (@RequestMapping, @RequestParam)
     */
    @Override
    public ResponseEntity<GetFactions200Response> getFactions(String origin) {
        log.info("GET /factions?origin={}", origin);
        GetFactions200Response response = factionsService.getFactions(origin);
        return ResponseEntity.ok(response);
    }
}

