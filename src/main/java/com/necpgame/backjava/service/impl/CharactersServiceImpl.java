package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.CharacterAppearanceEntity;
import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.mapper.CharacterAppearanceMapperMS;
import com.necpgame.backjava.mapper.CharacterMapperMS;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.*;
import com.necpgame.backjava.service.CharactersService;
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
 * Реализация сервиса персонажей
 * Использует MapStruct для автоматического маппинга с поддержкой JsonNullable
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
    
    /**
     * Список персонажей текущего игрока
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
        response.setMaxCharacters(5); // TODO: Вынести в конфигурацию
        response.setCurrentCount(summaries.size());
        
        return response;
    }
    
    /**
     * Создать нового персонажа
     */
    @Override
    @Transactional
    public CreateCharacter201Response createCharacter(CreateCharacterRequest request) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Creating character for account {}: {}", accountId, request.getName());
        
        // Создание персонажа
        CharacterEntity character = new CharacterEntity();
        character.setAccount(accountRepository.getReferenceById(accountId));
        character.setName(request.getName());
        character.setClassCode(request.getPropertyClass().getValue());
        // Преобразуем enum из DTO в enum Entity (используем getValue() чтобы получить lowercase значение)
        character.setGender(CharacterEntity.Gender.valueOf(request.getGender().getValue()));
        character.setOriginCode(request.getOrigin().getValue());
        
        // Опциональные поля с JsonNullable
        if (request.getSubclass() != null && request.getSubclass().isPresent()) {
            character.setSubclassCode(request.getSubclass().get());
        }
        if (request.getFactionId() != null && request.getFactionId().isPresent()) {
            character.setFaction(factionRepository.getReferenceById(request.getFactionId().get()));
        }
        if (request.getCityId() != null) {
            character.setCity(cityRepository.getReferenceById(request.getCityId()));
        }
        
        // Внешность через MapStruct
        // Appearance обязательное поле в Entity, поэтому всегда создаем его
        if (request.getAppearance() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, 
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
     * Получить детальную информацию о персонаже
     */
    @Transactional(readOnly = true)
    public GameCharacter getCharacter(UUID characterId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Getting character {} for account {}", characterId, accountId);
        
        CharacterEntity character = characterRepository.findById(characterId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Character not found: " + characterId));
        
        // Проверка владельца
        if (!character.getAccount().getId().equals(accountId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                "You don't have access to this character");
        }
        
        return characterMapper.toDto(character);
    }
    
    /**
     * Удалить персонажа
     */
    @Override
    @Transactional
    public DeleteCharacter200Response deleteCharacter(UUID characterId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        log.info("Deleting character {} for account {}", characterId, accountId);
        
        CharacterEntity character = characterRepository.findById(characterId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Character not found: " + characterId));
        
        // Проверка владельца
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
     * Список доступных классов персонажей
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
     * Список доступных происхождений персонажей
     */
    @Override
    @Transactional(readOnly = true)
    public GetCharacterOrigins200Response getCharacterOrigins() {
        log.info("Getting character origins");
        
        var origins = characterOriginRepository.findAll().stream()
            .map(entity -> {
                GameCharacterOrigin dto = new GameCharacterOrigin();
                // Используем enum из DTO
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

