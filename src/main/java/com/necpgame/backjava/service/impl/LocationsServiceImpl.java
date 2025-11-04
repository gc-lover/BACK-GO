package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.CityEntity;
import com.necpgame.backjava.mapper.CityMapper;
import com.necpgame.backjava.model.GetCities200Response;
import com.necpgame.backjava.repository.CityRepository;
import com.necpgame.backjava.service.LocationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация LocationsService - получение списка городов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsServiceImpl implements LocationsService {
    
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    
    /**
     * Получить список городов (с опциональной фильтрацией по фракции и региону)
     */
    @Override
    @Transactional(readOnly = true)
    public GetCities200Response getCities(UUID factionId, String region) {
        log.info("Fetching cities (factionId: {}, region: {})", factionId, region);
        
        List<CityEntity> cities;
        
        if (factionId != null && region != null) {
            cities = cityRepository.findByRegionAndFaction(region, factionId);
        } else if (factionId != null) {
            cities = cityRepository.findByAvailableForFaction(factionId);
        } else if (region != null) {
            cities = cityRepository.findByRegion(region);
        } else {
            cities = cityRepository.findAll();
        }
        
        GetCities200Response response = new GetCities200Response();
        response.setCities(cities.stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList()));
        
        return response;
    }
}

