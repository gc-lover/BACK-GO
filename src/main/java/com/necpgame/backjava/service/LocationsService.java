package com.necpgame.backjava.service;

import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetCities200Response;
import org.springframework.lang.Nullable;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for LocationsService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")public interface LocationsService {

    /**
     * GET /locations/cities : Список доступных городов
     * Получает список всех доступных городов. Может быть отфильтрован по фракции и региону.
     *
     * @param factionId Фильтр по фракции (опционально) (optional)
     * @param region Фильтр по региону/серверу (опционально) (optional)
     * @return GetCities200Response
     */
    GetCities200Response getCities(UUID factionId, String region);
}

