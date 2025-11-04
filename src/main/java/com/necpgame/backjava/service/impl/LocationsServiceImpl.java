package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.model.City;
import com.necpgame.backjava.model.GetCities200Response;
import com.necpgame.backjava.repository.CityRepository;
import com.necpgame.backjava.service.LocationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса локаций (городов)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsServiceImpl implements LocationsService {
    
    private final CityRepository cityRepository;
    
    /**
     * Получить список всех городов (с опциональной фильтрацией)
     */
    @Override
    @Transactional(readOnly = true)
    public GetCities200Response getCities(UUID factionId, String region) {
        log.info("Getting cities - factionId: {}, region: {}", factionId, region);
        
        try {
            // TODO: Implement filtering by factionId and region
            var cities = cityRepository.findAll().stream()
                .map(entity -> {
                    City dto = new City();
                    dto.setId(entity.getId());
                    dto.setName(entity.getName());
                    dto.setRegion(entity.getRegion());
                    dto.setDescription(entity.getDescription());
                    // TODO: Add factionId when relationship is clarified
                    return dto;
                })
                .collect(Collectors.toList());
            
            GetCities200Response response = new GetCities200Response();
            response.setCities(cities);
            
            log.info("Successfully fetched {} cities", cities.size());
            return response;
        } catch (Exception e) {
            log.error("Error fetching cities", e);
            throw e;
        }
    }
}

