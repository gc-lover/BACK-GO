package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.model.Character;
import com.necpgame.backjava.model.CharacterSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper для преобразования CharacterEntity ↔ Character DTO
 */
@Component
@RequiredArgsConstructor
public class CharacterMapper {
    
    private final CharacterAppearanceMapper appearanceMapper;
    
    /**
     * Преобразовать Entity в полный DTO
     */
    public Character toDto(CharacterEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Character dto = new Character();
        dto.setId(entity.getId());
        dto.setAccountId(entity.getAccount().getId());
        dto.setName(entity.getName());
        dto.setClazz(Character.ClazzEnum.fromValue(entity.getClassCode()));
        dto.setSubclass(entity.getSubclassCode());
        dto.setGender(Character.GenderEnum.fromValue(entity.getGender().name()));
        dto.setOrigin(Character.OriginEnum.fromValue(entity.getOriginCode()));
        
        // Faction
        if (entity.getFaction() != null) {
            dto.setFactionId(entity.getFaction().getId());
            dto.setFactionName(entity.getFaction().getName());
        }
        
        // City
        if (entity.getCity() != null) {
            dto.setCityId(entity.getCity().getId());
            dto.setCityName(entity.getCity().getName());
        }
        
        // Appearance
        dto.setAppearance(appearanceMapper.toDto(entity.getAppearance()));
        
        dto.setLevel(entity.getLevel());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastLogin(entity.getLastLogin());
        
        return dto;
    }
    
    /**
     * Преобразовать Entity в краткий DTO (для списка персонажей)
     */
    public CharacterSummary toSummaryDto(CharacterEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CharacterSummary dto = new CharacterSummary();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setClazz(entity.getClassCode());
        dto.setLevel(entity.getLevel());
        
        if (entity.getFaction() != null) {
            dto.setFactionName(entity.getFaction().getName());
        }
        
        if (entity.getCity() != null) {
            dto.setCityName(entity.getCity().getName());
        }
        
        dto.setLastLogin(entity.getLastLogin());
        
        return dto;
    }
}

