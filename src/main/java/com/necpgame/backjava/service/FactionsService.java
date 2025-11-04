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
     * GET /factions : Список доступных фракций
     * Получает список всех доступных фракций. Может быть отфильтрован по происхождению.
     *
     * @param origin Фильтр по происхождению (опционально) (optional)
     * @return GetFactions200Response
     */
    GetFactions200Response getFactions(String origin);
}

