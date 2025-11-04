package com.necpgame.backjava.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.CharacterOriginEntity;
import com.necpgame.backjava.model.CharacterOrigin;
import com.necpgame.backjava.model.CharacterOriginStartingResources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper для преобразования CharacterOriginEntity ↔ CharacterOrigin DTO
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CharacterOriginMapper {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Преобразовать Entity в DTO
     */
    public CharacterOrigin toDto(CharacterOriginEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CharacterOrigin dto = new CharacterOrigin();
        dto.setId(CharacterOrigin.IdEnum.fromValue(entity.getOriginCode()));
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        
        // Starting skills (JSON string -> List<String>)
        dto.setStartingSkills(parseJsonArray(entity.getStartingSkills()));
        
        // Available factions (UUID list)
        if (entity.getAvailableFactions() != null) {
            dto.setAvailableFactions(entity.getAvailableFactions().stream()
                .map(f -> f.getId())
                .collect(Collectors.toList()));
        }
        
        // Starting resources
        CharacterOriginStartingResources resources = new CharacterOriginStartingResources();
        resources.setCurrency(entity.getStartingCurrency());
        resources.setItems(parseJsonArray(entity.getStartingItems()));
        dto.setStartingResources(resources);
        
        return dto;
    }
    
    /**
     * Парсинг JSON массива в List<String>
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

