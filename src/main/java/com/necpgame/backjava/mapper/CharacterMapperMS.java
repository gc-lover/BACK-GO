package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.model.GameCharacter;
import com.necpgame.backjava.model.GameCharacterSummary;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * MapStruct маппер для преобразования CharacterEntity <-> GameCharacter DTO
 * Автоматически генерирует код маппинга с поддержкой JsonNullable
 */
@Mapper(
    componentModel = "spring",
    uses = {CharacterAppearanceMapperMS.class, JsonNullableMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CharacterMapperMS {
    
    /**
     * Преобразовать Entity в DTO
     */
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "classCode", target = "propertyClass", qualifiedByName = "stringToClassEnum")
    @Mapping(source = "subclassCode", target = "subclass", qualifiedByName = "stringToJsonNullable")
    @Mapping(source = "gender", target = "gender", qualifiedByName = "genderToEnum")
    @Mapping(source = "originCode", target = "origin", qualifiedByName = "stringToOriginEnum")
    @Mapping(source = "faction.id", target = "factionId", qualifiedByName = "uuidToJsonNullable")
    @Mapping(source = "faction.name", target = "factionName", qualifiedByName = "stringToJsonNullable")
    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "city.name", target = "cityName")
    @Mapping(source = "lastLogin", target = "lastLogin", qualifiedByName = "dateToJsonNullable")
    GameCharacter toDto(CharacterEntity entity);
    
    /**
     * Преобразовать Entity в краткий DTO (для списка персонажей)
     */
    @Mapping(source = "classCode", target = "propertyClass")
    @Mapping(source = "faction.name", target = "factionName", qualifiedByName = "stringToJsonNullable")
    @Mapping(source = "city.name", target = "cityName")
    @Mapping(source = "lastLogin", target = "lastLogin", qualifiedByName = "dateToJsonNullable")
    GameCharacterSummary toSummaryDto(CharacterEntity entity);
    
    // === Custom mapping methods для enum конверсий ===
    
    @Named("stringToClassEnum")
    default GameCharacter.PropertyClassEnum stringToClassEnum(String classCode) {
        return classCode != null ? GameCharacter.PropertyClassEnum.fromValue(classCode) : null;
    }
    
    @Named("genderToEnum")
    default GameCharacter.GenderEnum genderToEnum(CharacterEntity.Gender gender) {
        return gender != null ? GameCharacter.GenderEnum.fromValue(gender.name()) : null;
    }
    
    @Named("stringToOriginEnum")
    default GameCharacter.OriginEnum stringToOriginEnum(String originCode) {
        return originCode != null ? GameCharacter.OriginEnum.fromValue(originCode) : null;
    }
}

