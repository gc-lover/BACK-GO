package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.*;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.*;
import com.necpgame.backjava.service.GameStartService;
import com.necpgame.backjava.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GameStartServiceImpl - реализация сервиса запуска игры.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameStartServiceImpl implements GameStartService {

    private final GameSessionRepository gameSessionRepository;
    private final CharacterRepository characterRepository;
    private final LocationRepository locationRepository;
    private final TutorialProgressRepository tutorialProgressRepository;
    private final QuestProgressRepository questProgressRepository;

    private static final String STARTING_LOCATION_ID = "loc-downtown-001";
    private static final Integer STARTING_HEALTH = 100;
    private static final Integer STARTING_ENERGY = 100;
    private static final Integer STARTING_HUMANITY = 100;
    private static final Integer STARTING_MONEY = 500;
    private static final Integer STARTING_LEVEL = 1;

    @Override
    @Transactional
    public GameStartResponse startGame(GameStartRequest request) {
        log.info("Starting game for character: {}", request.getCharacterId());

        // 1. Проверяем существование персонажа
        CharacterEntity character = characterRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Character not found: " + request.getCharacterId()));

        // 2. Проверяем, что персонаж принадлежит текущему пользователю
        UUID currentAccountId = SecurityUtil.getCurrentAccountId();
        if (character.getAccount() == null || !character.getAccount().getId().equals(currentAccountId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, 
                "Character does not belong to the current account");
        }

        // 3. Деактивируем предыдущие сессии
        gameSessionRepository.deactivateAllSessionsByCharacterId(character.getId());

        // 4. Загружаем стартовую локацию
        LocationEntity location = locationRepository.findById(STARTING_LOCATION_ID)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Starting location not found: " + STARTING_LOCATION_ID));

        // 5. Создаем новую игровую сессию
        GameSessionEntity session = new GameSessionEntity();
        session.setAccountId(currentAccountId);
        session.setCharacterId(character.getId());
        session.setLocationId(location.getId());
        session.setTutorialEnabled(!request.getSkipTutorial());
        session.setSessionStart(LocalDateTime.now());
        session.setIsActive(true);
        session = gameSessionRepository.save(session);

        // 6. Создаем/обновляем прогресс туториала
        if (!request.getSkipTutorial()) {
            createOrUpdateTutorialProgress(character.getId(), false);
        } else {
            createOrUpdateTutorialProgress(character.getId(), true);
        }

        // 7. Обновляем состояние персонажа (если это первый старт)
        updateCharacterInitialState(character);

        // 8. Формируем ответ
        GameStartResponse response = new GameStartResponse();
        response.setGameSessionId(session.getId());
        response.setCharacterId(character.getId());
        response.setCurrentLocation(mapLocationToDto(location));
        response.setCharacterState(buildCharacterState(character));
        response.setStartingEquipment(getStartingEquipment());
        response.setWelcomeMessage(buildWelcomeMessage(character, location));
        response.setTutorialEnabled(!request.getSkipTutorial());

        log.info("Game started successfully. Session ID: {}", session.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public WelcomeScreenResponse getWelcomeScreen(UUID characterId) {
        log.info("Getting welcome screen for character: {}", characterId);

        // 1. Проверяем существование персонажа
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Character not found: " + characterId));

        // 2. Проверяем доступ
        UUID currentAccountId = SecurityUtil.getCurrentAccountId();
        if (character.getAccount() == null || !character.getAccount().getId().equals(currentAccountId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, 
                "Character does not belong to the current account");
        }

        // 3. Формируем ответ
        WelcomeScreenResponse response = new WelcomeScreenResponse();
        response.setMessage("Добро пожаловать в NECPGAME");
        response.setSubtitle("Ночь в Night City только начинается...");
        
        // Информация о персонаже
        WelcomeScreenResponse.WelcomeCharacterInfo charInfo = new WelcomeScreenResponse.WelcomeCharacterInfo();
        charInfo.setName(character.getName());
        charInfo.setCharacterClass(character.getClassCode() != null ? "Соло" : "Неизвестно");
        charInfo.setLevel(1);
        response.setCharacter(charInfo);
        
        response.setStartingLocation("Night City - Downtown");
        
        // Кнопки
        List<WelcomeScreenResponse.WelcomeButton> buttons = new ArrayList<>();
        
        WelcomeScreenResponse.WelcomeButton startButton = new WelcomeScreenResponse.WelcomeButton();
        startButton.setId("start-game");
        startButton.setLabel("Начать игру");
        buttons.add(startButton);
        
        WelcomeScreenResponse.WelcomeButton skipButton = new WelcomeScreenResponse.WelcomeButton();
        skipButton.setId("skip-tutorial");
        skipButton.setLabel("Пропустить туториал");
        buttons.add(skipButton);
        
        response.setButtons(buttons);

        return response;
    }

    @Override
    @Transactional
    public GameReturnResponse returnToGame(GameReturnRequest request) {
        log.info("Returning to game for character: {}", request.getCharacterId());

        // 1. Проверяем существование персонажа
        CharacterEntity character = characterRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "Character not found: " + request.getCharacterId()));

        // 2. Проверяем доступ
        UUID currentAccountId = SecurityUtil.getCurrentAccountId();
        if (character.getAccount() == null || !character.getAccount().getId().equals(currentAccountId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, 
                "Character does not belong to the current account");
        }

        // 3. Деактивируем предыдущие сессии
        gameSessionRepository.deactivateAllSessionsByCharacterId(character.getId());

        // 4. Определяем текущую локацию (из последней сессии или стартовая)
        String currentLocationId = gameSessionRepository
                .findByCharacterIdOrderByCreatedAtDesc(character.getId())
                .stream()
                .findFirst()
                .map(GameSessionEntity::getLocationId)
                .orElse(STARTING_LOCATION_ID);

        LocationEntity location = locationRepository.findById(currentLocationId)
                .orElseGet(() -> locationRepository.findById(STARTING_LOCATION_ID)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                            "Location not found")));

        // 5. Создаем новую сессию
        GameSessionEntity session = new GameSessionEntity();
        session.setAccountId(currentAccountId);
        session.setCharacterId(character.getId());
        session.setLocationId(location.getId());
        session.setTutorialEnabled(false);
        session.setSessionStart(LocalDateTime.now());
        session.setIsActive(true);
        session = gameSessionRepository.save(session);

        // 6. Загружаем активные квесты
        List<GameActiveQuest> activeQuests = questProgressRepository
                .findActiveQuestsByCharacterId(character.getId())
                .stream()
                .map(qp -> {
                    GameActiveQuest quest = new GameActiveQuest();
                    quest.setQuestId(qp.getQuestId());
                    quest.setProgress(qp.getProgress());
                    return quest;
                })
                .collect(Collectors.toList());

        // 7. Формируем ответ
        GameReturnResponse response = new GameReturnResponse();
        response.setGameSessionId(session.getId());
        response.setCharacterId(character.getId());
        response.setCurrentLocation(mapLocationToDto(location));
        response.setCharacterState(buildCharacterState(character));
        response.setActiveQuests(activeQuests);

        log.info("Returned to game successfully. Session ID: {}", session.getId());
        return response;
    }

    // ======================== Helper Methods ========================

    private void createOrUpdateTutorialProgress(UUID characterId, boolean skipped) {
        TutorialProgressEntity progress = tutorialProgressRepository.findByCharacterId(characterId)
                .orElse(new TutorialProgressEntity());
        
        progress.setCharacterId(characterId);
        progress.setCurrentStep(0);
        progress.setTotalSteps(4);
        progress.setSkipped(skipped);
        progress.setCompleted(skipped);
        
        if (skipped) {
            progress.setCompletedAt(LocalDateTime.now());
        }
        
        tutorialProgressRepository.save(progress);
    }

    private void updateCharacterInitialState(CharacterEntity character) {
        // Устанавливаем начальные характеристики, если они еще не установлены
        // В реальном приложении можно добавить проверку на первый запуск
    }

    private GameCharacterState buildCharacterState(CharacterEntity character) {
        GameCharacterState state = new GameCharacterState();
        state.setHealth(STARTING_HEALTH);
        state.setEnergy(STARTING_ENERGY);
        state.setHumanity(STARTING_HUMANITY);
        state.setMoney(STARTING_MONEY);
        state.setLevel(STARTING_LEVEL);
        state.setExperience(0);
        return state;
    }

    private List<GameStartingItem> getStartingEquipment() {
        List<GameStartingItem> equipment = new ArrayList<>();
        
        GameStartingItem pistol = new GameStartingItem();
        pistol.setItemId("item-pistol-liberty");
        pistol.setQuantity(1);
        equipment.add(pistol);
        
        GameStartingItem armor = new GameStartingItem();
        armor.setItemId("item-armor-street");
        armor.setQuantity(1);
        equipment.add(armor);
        
        return equipment;
    }

    private String buildWelcomeMessage(CharacterEntity character, LocationEntity location) {
        return String.format("Добро пожаловать в Night City, %s. Вы стоите в центре корпоративного района Downtown. " +
                "Неоновые вывески мигают на стенах зданий. Ваше приключение начинается...", 
                character.getName());
    }

    private GameLocation mapLocationToDto(LocationEntity entity) {
        GameLocation dto = new GameLocation();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setDangerLevel(mapDangerLevel(entity.getDangerLevel()));
        dto.setMinLevel(entity.getMinLevel());
        dto.setType(mapLocationType(entity.getType()));
        dto.setConnectedLocations(parseConnectedLocations(entity.getConnectedLocations()));
        return dto;
    }

    private GameLocation.DangerLevelEnum mapDangerLevel(LocationEntity.DangerLevel dangerLevel) {
        if (dangerLevel == null) return null;
        return switch (dangerLevel) {
            case LOW -> GameLocation.DangerLevelEnum.LOW;
            case MEDIUM -> GameLocation.DangerLevelEnum.MEDIUM;
            case HIGH -> GameLocation.DangerLevelEnum.HIGH;
        };
    }

    private GameLocation.TypeEnum mapLocationType(LocationEntity.LocationType type) {
        if (type == null) return null;
        return switch (type) {
            case CORPORATE -> GameLocation.TypeEnum.CORPORATE;
            case INDUSTRIAL -> GameLocation.TypeEnum.INDUSTRIAL;
            case RESIDENTIAL -> GameLocation.TypeEnum.RESIDENTIAL;
            case CRIMINAL -> GameLocation.TypeEnum.CRIMINAL;
        };
    }

    private List<String> parseConnectedLocations(String connectedLocations) {
        if (connectedLocations == null || connectedLocations.isEmpty()) {
            return new ArrayList<>();
        }
        // Simple JSON array parsing: ["loc-1", "loc-2"]
        return Arrays.stream(connectedLocations.replaceAll("[\\[\\]\"]", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}

