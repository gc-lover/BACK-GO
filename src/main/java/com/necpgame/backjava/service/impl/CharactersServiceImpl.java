package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.*;
import com.necpgame.backjava.exception.BadRequestException;
import com.necpgame.backjava.exception.ConflictException;
import com.necpgame.backjava.exception.ResourceNotFoundException;
import com.necpgame.backjava.mapper.CharacterAppearanceMapper;
import com.necpgame.backjava.mapper.CharacterClassMapper;
import com.necpgame.backjava.mapper.CharacterMapper;
import com.necpgame.backjava.mapper.CharacterOriginMapper;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.*;
import com.necpgame.backjava.service.CharactersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация CharactersService - CRUD операции с персонажами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharactersServiceImpl implements CharactersService {
    
    private final CharacterRepository characterRepository;
    private final AccountRepository accountRepository;
    private final CityRepository cityRepository;
    private final FactionRepository factionRepository;
    private final CharacterClassRepository characterClassRepository;
    private final CharacterOriginRepository characterOriginRepository;
    private final CharacterAppearanceRepository appearanceRepository;
    
    private final CharacterMapper characterMapper;
    private final CharacterClassMapper characterClassMapper;
    private final CharacterOriginMapper characterOriginMapper;
    private final CharacterAppearanceMapper appearanceMapper;
    
    /**
     * Получить список персонажей игрока
     */
    @Override
    @Transactional(readOnly = true)
    public ListCharacters200Response listCharacters() {
        UUID accountId = getCurrentAccountId();
        log.info("Fetching characters for account: {}", accountId);
        
        List<CharacterEntity> characters = characterRepository.findAllByAccountId(accountId);
        
        ListCharacters200Response response = new ListCharacters200Response();
        response.setCharacters(characters.stream()
                .map(characterMapper::toSummaryDto)
                .collect(Collectors.toList()));
        
        return response;
    }
    
    /**
     * Создать нового персонажа
     */
    @Override
    @Transactional
    public CreateCharacter201Response createCharacter(CreateCharacterRequest request) {
        UUID accountId = getCurrentAccountId();
        log.info("Creating character '{}' for account: {}", request.getName(), accountId);
        
        // Валидация: имя уже существует у этого аккаунта
        if (characterRepository.existsByNameAndAccountId(request.getName(), accountId)) {
            throw new ConflictException("Character with name '" + request.getName() + "' already exists");
        }
        
        // Получение аккаунта
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        // Получение города
        CityEntity city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId()));
        
        // Получение фракции (если указана)
        FactionEntity faction = null;
        if (request.getFactionId() != null) {
            faction = factionRepository.findById(request.getFactionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faction", "id", request.getFactionId()));
        }
        
        // Создание внешности
        CharacterAppearanceEntity appearance = appearanceMapper.toEntity(request.getAppearance());
        appearance = appearanceRepository.save(appearance);
        
        // Создание персонажа
        CharacterEntity character = new CharacterEntity();
        character.setAccount(account);
        character.setName(request.getName());
        character.setClassCode(request.getClazz().getValue());
        character.setSubclassCode(request.getSubclass());
        character.setGender(CharacterEntity.Gender.valueOf(request.getGender().name()));
        character.setOriginCode(request.getOrigin().getValue());
        character.setFaction(faction);
        character.setCity(city);
        character.setAppearance(appearance);
        character.setLevel(1);
        character.setCreatedAt(OffsetDateTime.now());
        
        character = characterRepository.save(character);
        
        log.info("Character created: {}", character.getId());
        
        // Формирование ответа
        CreateCharacter201Response response = new CreateCharacter201Response();
        response.setCharacterId(character.getId());
        response.setMessage("Character created successfully");
        
        return response;
    }
    
    /**
     * Получить детальную информацию о персонаже
     */
    @Override
    @Transactional(readOnly = true)
    public Character getCharacter(UUID characterId) {
        UUID accountId = getCurrentAccountId();
        log.info("Fetching character {} for account: {}", characterId, accountId);
        
        CharacterEntity character = characterRepository.findByIdWithDetails(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "id", characterId));
        
        // Проверка владения персонажем
        if (!character.getAccount().getId().equals(accountId)) {
            throw new ResourceNotFoundException("Character", "id", characterId);
        }
        
        return characterMapper.toDto(character);
    }
    
    /**
     * Удалить персонажа
     */
    @Override
    @Transactional
    public DeleteCharacter200Response deleteCharacter(UUID characterId) {
        UUID accountId = getCurrentAccountId();
        log.info("Deleting character {} for account: {}", characterId, accountId);
        
        CharacterEntity character = characterRepository.findByIdAndAccountId(characterId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "id", characterId));
        
        characterRepository.delete(character);
        
        log.info("Character deleted: {}", characterId);
        
        DeleteCharacter200Response response = new DeleteCharacter200Response();
        response.setMessage("Character deleted successfully");
        
        return response;
    }
    
    /**
     * Получить список доступных классов
     */
    @Override
    @Transactional(readOnly = true)
    public GetCharacterClasses200Response getCharacterClasses() {
        log.info("Fetching character classes");
        
        List<CharacterClassEntity> classes = characterClassRepository.findAllWithSubclasses();
        
        GetCharacterClasses200Response response = new GetCharacterClasses200Response();
        response.setClasses(classes.stream()
                .map(characterClassMapper::toDto)
                .collect(Collectors.toList()));
        
        return response;
    }
    
    /**
     * Получить список доступных происхождений
     */
    @Override
    @Transactional(readOnly = true)
    public GetCharacterOrigins200Response getCharacterOrigins() {
        log.info("Fetching character origins");
        
        List<CharacterOriginEntity> origins = characterOriginRepository.findAllWithFactions();
        
        GetCharacterOrigins200Response response = new GetCharacterOrigins200Response();
        response.setOrigins(origins.stream()
                .map(characterOriginMapper::toDto)
                .collect(Collectors.toList()));
        
        return response;
    }
    
    /**
     * Получить текущий Account ID из Security Context
     */
    private UUID getCurrentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        return UUID.fromString(authentication.getName());
    }
}

