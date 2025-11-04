package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.CharacterAppearanceEntity;
import com.necpgame.backjava.model.GameCharacterAppearance;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * MapStruct маппер для преобразования CharacterAppearanceEntity <-> GameCharacterAppearance DTO
 * Автоматически генерирует код маппинга с поддержкой JsonNullable
 */
@Mapper(
    componentModel = "spring",
    uses = {JsonNullableMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CharacterAppearanceMapperMS {
    
    /**
     * Преобразовать Entity в DTO
     */
    @Mapping(source = "bodyType", target = "bodyType", qualifiedByName = "bodyTypeToEnum")
    @Mapping(source = "distinctiveFeatures", target = "distinctiveFeatures", qualifiedByName = "stringToJsonNullable")
    GameCharacterAppearance toDto(CharacterAppearanceEntity entity);
    
    /**
     * Преобразовать DTO в Entity
     */
    @Mapping(source = "bodyType", target = "bodyType", qualifiedByName = "enumToBodyType")
    @Mapping(source = "distinctiveFeatures", target = "distinctiveFeatures", qualifiedByName = "jsonNullableToString")
    CharacterAppearanceEntity toEntity(GameCharacterAppearance dto);
    
    // === Custom mapping methods для enum конверсий ===
    
    @Named("bodyTypeToEnum")
    default GameCharacterAppearance.BodyTypeEnum bodyTypeToEnum(CharacterAppearanceEntity.BodyType bodyType) {
        return bodyType != null ? GameCharacterAppearance.BodyTypeEnum.fromValue(bodyType.name()) : null;
    }
    
    @Named("enumToBodyType")
    default CharacterAppearanceEntity.BodyType enumToBodyType(GameCharacterAppearance.BodyTypeEnum bodyType) {
        return bodyType != null ? CharacterAppearanceEntity.BodyType.valueOf(bodyType.name()) : null;
    }
}

