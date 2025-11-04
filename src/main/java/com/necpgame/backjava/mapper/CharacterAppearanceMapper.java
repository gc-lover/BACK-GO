package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.CharacterAppearanceEntity;
import com.necpgame.backjava.model.CharacterAppearance;
import org.springframework.stereotype.Component;

/**
 * Mapper для преобразования CharacterAppearanceEntity ↔ CharacterAppearance DTO
 */
@Component
public class CharacterAppearanceMapper {
    
    /**
     * Преобразовать Entity в DTO
     */
    public CharacterAppearance toDto(CharacterAppearanceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CharacterAppearance dto = new CharacterAppearance();
        dto.setHeight(entity.getHeight());
        dto.setBodyType(CharacterAppearance.BodyTypeEnum.fromValue(entity.getBodyType().name()));
        dto.setHairColor(entity.getHairColor());
        dto.setEyeColor(entity.getEyeColor());
        dto.setSkinColor(entity.getSkinColor());
        dto.setDistinctiveFeatures(entity.getDistinctiveFeatures());
        
        return dto;
    }
    
    /**
     * Преобразовать DTO в Entity
     */
    public CharacterAppearanceEntity toEntity(CharacterAppearance dto) {
        if (dto == null) {
            return null;
        }
        
        CharacterAppearanceEntity entity = new CharacterAppearanceEntity();
        entity.setHeight(dto.getHeight());
        entity.setBodyType(CharacterAppearanceEntity.BodyType.valueOf(dto.getBodyType().name()));
        entity.setHairColor(dto.getHairColor());
        entity.setEyeColor(dto.getEyeColor());
        entity.setSkinColor(dto.getSkinColor());
        entity.setDistinctiveFeatures(dto.getDistinctiveFeatures());
        
        return entity;
    }
}

