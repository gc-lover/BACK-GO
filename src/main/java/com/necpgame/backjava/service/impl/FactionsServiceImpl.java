package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.FactionEntity;
import com.necpgame.backjava.mapper.FactionMapper;
import com.necpgame.backjava.model.GetFactions200Response;
import com.necpgame.backjava.repository.FactionRepository;
import com.necpgame.backjava.service.FactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация FactionsService - получение списка фракций
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FactionsServiceImpl implements FactionsService {
    
    private final FactionRepository factionRepository;
    private final FactionMapper factionMapper;
    
    /**
     * Получить список фракций (с опциональной фильтрацией по происхождению)
     */
    @Override
    @Transactional(readOnly = true)
    public GetFactions200Response getFactions(String origin) {
        log.info("Fetching factions{}", origin != null ? " for origin: " + origin : "");
        
        List<FactionEntity> factions;
        
        if (origin != null) {
            factions = factionRepository.findByAvailableForOrigin(origin);
        } else {
            factions = factionRepository.findAll();
        }
        
        GetFactions200Response response = new GetFactions200Response();
        response.setFactions(factions.stream()
                .map(factionMapper::toDto)
                .collect(Collectors.toList()));
        
        return response;
    }
}

