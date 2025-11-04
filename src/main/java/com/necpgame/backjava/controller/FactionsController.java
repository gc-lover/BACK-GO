package com.necpgame.backjava.controller;

import com.necpgame.backjava.model.GetFactions200Response;
import com.necpgame.backjava.service.FactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller для работы с фракциями
 */
@Slf4j
@RestController
@RequestMapping("/factions")
@RequiredArgsConstructor
public class FactionsController {
    
    private final FactionsService factionsService;
    
    /**
     * GET /factions - Список доступных фракций
     */
    @GetMapping
    public ResponseEntity<GetFactions200Response> getFactions(
            @RequestParam(required = false) String origin) {
        log.info("GET /factions?origin={}", origin);
        GetFactions200Response response = factionsService.getFactions(origin);
        return ResponseEntity.ok(response);
    }
}

