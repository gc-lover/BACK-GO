package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.model.Faction;
import com.necpgame.backjava.model.GetFactions200Response;
import com.necpgame.backjava.repository.FactionRepository;
import com.necpgame.backjava.repository.specification.LoreFactionSpecifications;
import com.necpgame.backjava.service.FactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import com.necpgame.backjava.model.LoreFactionType;
import com.necpgame.backjava.model.LoreFactionEntity;
import org.springframework.util.StringUtils;

/**
 * Р РµР°Р»РёР·Р°С†РёСЏ СЃРµСЂРІРёСЃР° С„СЂР°РєС†РёР№
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FactionsServiceImpl implements FactionsService {
    
    private final FactionRepository factionRepository;
    
    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє РІСЃРµС… С„СЂР°РєС†РёР№ (СЃ РѕРїС†РёРѕРЅР°Р»СЊРЅРѕР№ С„РёР»СЊС‚СЂР°С†РёРµР№ РїРѕ РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёСЋ)
     */
    @Override
    @Transactional(readOnly = true)
    public GetFactions200Response getFactions(String origin) {
        log.info("Getting factions for origin: {}", origin);
        
        try {
            // TODO: Implement filtering by origin
            var factions = factionRepository.findAll().stream()
                .map(entity -> {
                    Faction dto = new Faction();
                    dto.setId(entity.getId());
                    dto.setName(entity.getName());
                    // РљРѕРЅРІРµСЂС‚РёСЂСѓРµРј Entity.FactionType РІ DTO.TypeEnum
                    if (entity.getType() != null) {
                        String typeName = entity.getType().name(); // corporation, gang, organization
                        dto.setType(Faction.TypeEnum.fromValue(typeName));
                    }
                    dto.setDescription(entity.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
            
            GetFactions200Response response = new GetFactions200Response();
            response.setFactions(factions);
            
            log.info("Successfully fetched {} factions", factions.size());
            return response;
        } catch (Exception e) {
            log.error("Error fetching factions", e);
            throw e;
        }
    }
}

