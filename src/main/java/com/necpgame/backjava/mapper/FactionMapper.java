package com.necpgame.backjava.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.FactionEntity;
import com.necpgame.backjava.model.Faction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper РґР»СЏ РїСЂРµРѕР±СЂР°Р·РѕРІР°РЅРёСЏ FactionEntity в†” Faction DTO
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FactionMapper {
    
    private final ObjectMapper objectMapper;
    
    /**
     * РџСЂРµРѕР±СЂР°Р·РѕРІР°С‚СЊ Entity РІ DTO
     */
    public Faction toDto(FactionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Faction dto = new Faction();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(Faction.TypeEnum.fromValue(entity.getType().name()));
        dto.setDescription(entity.getDescription());
        
        // Available for origins (JSON string -> List<String>)
        List<String> originsJson = parseJsonArray(entity.getAvailableForOrigins());
        dto.setAvailableForOrigins(originsJson.stream()
            .map(s -> Faction.AvailableForOriginsEnum.fromValue(s))
            .collect(Collectors.toList()));
        
        return dto;
    }
    
    /**
     * РџР°СЂСЃРёРЅРі JSON РјР°СЃСЃРёРІР° РІ List<String>
     */
    private List<String> parseJsonArray(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse JSON array: {}", json, e);
            return new ArrayList<>();
        }
    }
}

