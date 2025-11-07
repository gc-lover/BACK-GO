package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.CharacterAppearanceEntity;
import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.mapper.CharacterAppearanceMapperMS;
import com.necpgame.backjava.mapper.CharacterMapperMS;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.*;
import com.necpgame.backjava.repository.LoreCharacterCategoryRepository;
import com.necpgame.backjava.service.CharactersService;
import com.necpgame.backjava.service.mapper.LoreMapper;
import com.necpgame.backjava.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Р РµР°Р»РёР·Р°С†РёСЏ СЃРµСЂРІРёСЃР° РїРµСЂСЃРѕРЅР°Р¶РµР№
 * РСЃРїРѕР»СЊР·СѓРµС‚ MapStruct РґР»СЏ Р°РІС‚РѕРјР°С‚РёС‡РµСЃРєРѕРіРѕ РјР°РїРїРёРЅРіР° СЃ РїРѕРґРґРµСЂР¶РєРѕР№ JsonNullable
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharactersServiceImpl implements CharactersService {
    
    private final CharacterRepository characterRepository;
    private final AccountRepository accountRepository;
    private final CharacterClassRepository characterClassRepository;
    private final CharacterSubclassRepository characterSubclassRepository;
    private final CharacterOriginRepository characterOriginRepository;
    private final FactionRepository factionRepository;
    private final CityRepository cityRepository;
    private final CharacterMapperMS characterMapper;
    private final CharacterAppearanceMapperMS appearanceMapper;
    private final LoreCharacterCategoryRepository loreCharacterCategoryRepository;
    private final LoreMapper loreMapper;
    @Override
    @Transactional(readOnly = true)
    public GetCharacterCategories200Response getCharacterCategories() {
        var categories = loreCharacterCategoryRepository.findAll().stream()
            .map(loreMapper::toCharacterCategory)
            .collect(Collectors.toList());

        GetCharacterCategories200Response response = new GetCharacterCategories200Response();
        response.setCategories(categories);
        return response;
    }

    
    /**
     * РЎРїРёСЃРѕРє РїРµСЂСЃРѕРЅР°Р¶РµР№ С‚РµРєСѓС‰РµРіРѕ РёРіСЂРѕРєР°
     */
    @Override
    @Transactional(readOnly = true)
    public ListCharacters200Response listCharacters() {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Listing characters for account: {}", accountId);
        
        List<CharacterEntity> characters = characterRepository.findAllByAccountId(accountId);
        
        List<GameCharacterSummary> summaries = characters.stream()
            .map(characterMapper::toSummaryDto)
            .collect(Collectors.toList());
        
        ListCharacters200Response response = new ListCharacters200Response();
        response.setCharacters(summaries);
        response.setMaxCharacters(5); // TODO: Р’С‹РЅРµСЃС‚Рё РІ РєРѕРЅС„РёРіСѓСЂР°С†РёСЋ
        response.setCurrentCount(summaries.size());
        
        return response;
    }
    
    /**
     * РЎРѕР·РґР°С‚СЊ РЅРѕРІРѕРіРѕ РїРµСЂСЃРѕРЅР°Р¶Р°
     */
    @Override
    @Transactional
    public CreateCharacter201Response createCharacter(CreateCharacterRequest request) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Creating character for account {}: {}", accountId, request.getName());
        
        // РЎРѕР·РґР°РЅРёРµ РїРµСЂСЃРѕРЅР°Р¶Р°
        CharacterEntity character = new CharacterEntity();
        character.setAccount(accountRepository.getReferenceById(accountId));
        character.setName(request.getName());
        character.setClassCode(request.getPropertyClass().getValue());
        // РџСЂРµРѕР±СЂР°Р·СѓРµРј enum РёР· DTO РІ enum Entity (РёСЃРїРѕР»СЊР·СѓРµРј getValue() С‡С‚РѕР±С‹ РїРѕР»СѓС‡РёС‚СЊ lowercase Р·РЅР°С‡РµРЅРёРµ)
        character.setGender(CharacterEntity.Gender.valueOf(request.getGender().getValue()));
        character.setOriginCode(request.getOrigin().getValue());
        
        // РћРїС†РёРѕРЅР°Р»СЊРЅС‹Рµ РїРѕР»СЏ СЃ JsonNullable
        if (request.getSubclass() != null && request.getSubclass().isPresent()) {
            character.setSubclassCode(request.getSubclass().get());
        }
        if (request.getFactionId() != null && request.getFactionId().isPresent()) {
            character.setFaction(factionRepository.getReferenceById(request.getFactionId().get()));
        }
        if (request.getCityId() != null) {
            character.setCity(cityRepository.getReferenceById(request.getCityId()));
        }
        
        // Р’РЅРµС€РЅРѕСЃС‚СЊ С‡РµСЂРµР· MapStruct
        // Appearance РѕР±СЏР·Р°С‚РµР»СЊРЅРѕРµ РїРѕР»Рµ РІ Entity, РїРѕСЌС‚РѕРјСѓ РІСЃРµРіРґР° СЃРѕР·РґР°РµРј РµРіРѕ
        if (request.getAppearance() == null) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD, 
                "Appearance is required");
        }
        
        CharacterAppearanceEntity appearance = appearanceMapper.toEntity(request.getAppearance());
        character.setAppearance(appearance);
        
        character.setLevel(1);
        character.setCreatedAt(OffsetDateTime.now());
        character.setLastLogin(OffsetDateTime.now());
        character = characterRepository.save(character);
        
        log.info("Character created successfully: {}", character.getId());
        
        GameCharacter characterDto = characterMapper.toDto(character);
        CreateCharacter201Response response = new CreateCharacter201Response();
        response.setCharacter(characterDto);
        
        return response;
    }
    
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РґРµС‚Р°Р»СЊРЅСѓСЋ РёРЅС„РѕСЂРјР°С†РёСЋ Рѕ РїРµСЂСЃРѕРЅР°Р¶Рµ
     */
    @Transactional(readOnly = true)
    public GameCharacter getCharacter(UUID characterId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Getting character {} for account {}", characterId, accountId);
        
        CharacterEntity character = characterRepository.findById(characterId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Character not found: " + characterId));
        
        // РџСЂРѕРІРµСЂРєР° РІР»Р°РґРµР»СЊС†Р°
        if (!character.getAccount().getId().equals(accountId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                "You don't have access to this character");
        }
        
        return characterMapper.toDto(character);
    }
    
    /**
     * РЈРґР°Р»РёС‚СЊ РїРµСЂСЃРѕРЅР°Р¶Р°
     */
    @Override
    @Transactional
    public DeleteCharacter200Response deleteCharacter(UUID characterId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Deleting character {} for account {}", characterId, accountId);
        
        CharacterEntity character = characterRepository.findById(characterId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Character not found: " + characterId));
        
        // РџСЂРѕРІРµСЂРєР° РІР»Р°РґРµР»СЊС†Р°
        if (!character.getAccount().getId().equals(accountId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, 
                "You don't have access to this character");
        }
        
        characterRepository.delete(character);
        log.info("Character deleted successfully: {}", characterId);
        
        DeleteCharacter200Response response = new DeleteCharacter200Response();
        response.setMessage("Character deleted successfully");
        
        return response;
    }
    
    /**
     * РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… РєР»Р°СЃСЃРѕРІ РїРµСЂСЃРѕРЅР°Р¶РµР№
     */
    @Override
    @Transactional(readOnly = true)
    public GetCharacterClasses200Response getCharacterClasses() {
        log.info("Getting character classes");
        
        var classes = characterClassRepository.findAll().stream()
            .map(entity -> {
                GameCharacterClass dto = new GameCharacterClass();
                dto.setId(entity.getClassCode());
                dto.setName(entity.getName());
                dto.setDescription(entity.getDescription());
                dto.setSubclasses(new ArrayList<>()); // TODO: load subclasses
                return dto;
            })
            .collect(Collectors.toList());
        
        GetCharacterClasses200Response response = new GetCharacterClasses200Response();
        response.setClasses(classes);
        
        return response;
    }
    
    /**
     * РЎРїРёСЃРѕРє РґРѕСЃС‚СѓРїРЅС‹С… РїСЂРѕРёСЃС…РѕР¶РґРµРЅРёР№ РїРµСЂСЃРѕРЅР°Р¶РµР№
     */
    @Override
    @Transactional(readOnly = true)
    public GetCharacterOrigins200Response getCharacterOrigins() {
        log.info("Getting character origins");
        
        var origins = characterOriginRepository.findAll().stream()
            .map(entity -> {
                GameCharacterOrigin dto = new GameCharacterOrigin();
                // РСЃРїРѕР»СЊР·СѓРµРј enum РёР· DTO
                GameCharacterOrigin.IdEnum idEnum = GameCharacterOrigin.IdEnum.fromValue(entity.getOriginCode());
                dto.setId(idEnum);
                dto.setName(entity.getName());
                dto.setDescription(entity.getDescription());
                dto.setStartingSkills(new ArrayList<>()); // TODO: parse from JSON
                dto.setAvailableFactions(new ArrayList<>()); // TODO: load factions
                dto.setStartingResources(null); // TODO: map starting resources
                return dto;
            })
            .collect(Collectors.toList());
        
        GetCharacterOrigins200Response response = new GetCharacterOrigins200Response();
        response.setOrigins(origins);
        
        return response;
    }
}

