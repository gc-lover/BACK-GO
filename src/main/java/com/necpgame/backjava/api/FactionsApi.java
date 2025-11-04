package com.necpgame.backjava.api;

import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetFactions200Response;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for FactionsApi.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")public interface FactionsApi {

    /**
     * GET /factions : Список доступных фракций
     * Получает список всех доступных фракций. Может быть отфильтрован по происхождению.
     *
     * @param origin Фильтр по происхождению (опционально) (optional)
     * @return GetFactions200Response
     */
    GetFactions200Response getFactions(String origin);
}

