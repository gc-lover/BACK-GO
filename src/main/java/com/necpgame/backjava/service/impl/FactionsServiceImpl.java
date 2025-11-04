package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.model.Faction;
import com.necpgame.backjava.model.GetFactions200Response;
import com.necpgame.backjava.repository.FactionRepository;
import com.necpgame.backjava.service.FactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Реализация сервиса фракций
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FactionsServiceImpl implements FactionsService {
    
    private final FactionRepository factionRepository;
    
    /**
     * Получить список всех фракций (с опциональной фильтрацией по происхождению)
     */
    @Override
    @Transactional(readOnly = true)
    public GetFactions200Response getFactions(String origin) {
        log.info("Getting factions for origin: {}", origin);
        
        // TODO: Implement filtering by origin
        var factions = factionRepository.findAll().stream()
            .map(entity -> {
                Faction dto = new Faction();
                dto.setId(entity.getId());
                dto.setName(entity.getName());
                // Конвертируем Entity.FactionType в DTO.TypeEnum
                if (entity.getType() != null) {
                    dto.setType(Faction.TypeEnum.fromValue(entity.getType().name().toLowerCase()));
                }
                dto.setDescription(entity.getDescription());
                return dto;
            })
            .collect(Collectors.toList());
        
        GetFactions200Response response = new GetFactions200Response();
        response.setFactions(factions);
        
        return response;
    }
}

