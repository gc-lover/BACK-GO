package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.*;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.*;
import com.necpgame.backjava.service.GameInitialStateService;
import com.necpgame.backjava.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GameInitialStateServiceImpl - реализация сервиса начального состояния игры.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameInitialStateServiceImpl implements GameInitialStateService {

    private final CharacterRepository characterRepository;
    private final LocationRepository locationRepository;
    private final NPCRepository npcRepository;
    private final QuestRepository questRepository;
    private final TutorialProgressRepository tutorialProgressRepository;
    private final GameSessionRepository gameSessionRepository;

    private static final String STARTING_LOCATION_ID = "loc-downtown-001";
    private static final String FIRST_QUEST_ID = "quest-delivery-001";

    @Override
    @Transactional(readOnly = true)
    public InitialStateResponse getInitialState(UUID characterId) {
        log.info("Getting initial state for character: {}", characterId);

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

        // 3. Определяем текущую локацию
        String currentLocationId = gameSessionRepository
                .findByCharacterIdAndIsActiveTrue(characterId)
                .map(GameSessionEntity::getLocationId)
                .orElse(STARTING_LOCATION_ID);

        LocationEntity location = locationRepository.findById(currentLocationId)
                .orElse(locationRepository.findById(STARTING_LOCATION_ID)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                            "Location not found")));

        // 4. Загружаем NPC в локации
        List<NPCEntity> npcs = npcRepository.findByLocationId(location.getId());

        // 5. Загружаем первый квест
        QuestEntity firstQuest = questRepository.findById(FIRST_QUEST_ID)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                    "First quest not found: " + FIRST_QUEST_ID));

        // 6. Формируем доступные действия
        List<GameAction> actions = buildAvailableActions();

        // 7. Формируем ответ
        InitialStateResponse response = new InitialStateResponse();
        response.setLocation(mapLocationToDto(location));
        response.setAvailableNPCs(npcs.stream().map(this::mapNPCToDto).collect(Collectors.toList()));
        response.setFirstQuest(mapQuestToDto(firstQuest));
        response.setAvailableActions(actions);

        log.info("Initial state loaded successfully for character: {}", characterId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TutorialStepsResponse getTutorialSteps(UUID characterId) {
        log.info("Getting tutorial steps for character: {}", characterId);

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

        // 3. Загружаем прогресс туториала
        TutorialProgressEntity progress = tutorialProgressRepository.findByCharacterId(characterId)
                .orElse(createDefaultTutorialProgress(characterId));

        // 4. Формируем список шагов
        List<TutorialStep> steps = buildTutorialSteps();

        // 5. Формируем ответ
        TutorialStepsResponse response = new TutorialStepsResponse();
        response.setSteps(steps);
        response.setCurrentStep(progress.getCurrentStep());
        response.setTotalSteps(progress.getTotalSteps());
        response.setCanSkip(true);

        return response;
    }

    // ======================== Helper Methods ========================

    private TutorialProgressEntity createDefaultTutorialProgress(UUID characterId) {
        TutorialProgressEntity progress = new TutorialProgressEntity();
        progress.setCharacterId(characterId);
        progress.setCurrentStep(0);
        progress.setTotalSteps(4);
        progress.setCompleted(false);
        progress.setSkipped(false);
        return progress;
    }

    private List<TutorialStep> buildTutorialSteps() {
        List<TutorialStep> steps = new ArrayList<>();

        TutorialStep step1 = new TutorialStep();
        step1.setId("step-1");
        step1.setTitle("Добро пожаловать");
        step1.setDescription("Это ваш первый день в Night City. Вы находитесь в корпоративном районе Downtown.");
        step1.setHint("Изучите интерфейс и выберите действие");
        steps.add(step1);

        TutorialStep step2 = new TutorialStep();
        step2.setId("step-2");
        step2.setTitle("Осмотрите локацию");
        step2.setDescription("Изучите описание локации Downtown. Обратите внимание на уровень опасности и доступные переходы.");
        step2.setHint("Нажмите 'Осмотреть окрестности'");
        steps.add(step2);

        TutorialStep step3 = new TutorialStep();
        step3.setId("step-3");
        step3.setTitle("Поговорите с NPC");
        step3.setDescription("Найдите офицера Сару Миллер и поговорите с ней. Она даст вам первое задание.");
        step3.setHint("Выберите 'Поговорить с NPC' и найдите Сару Миллер");
        steps.add(step3);

        TutorialStep step4 = new TutorialStep();
        step4.setId("step-4");
        step4.setTitle("Примите первый квест");
        step4.setDescription("Сара Миллер даст вам первое задание 'Доставка груза'. Примите его, чтобы начать.");
        step4.setHint("Выберите опцию 'Какие задания у тебя есть?'");
        steps.add(step4);

        return steps;
    }

    private List<GameAction> buildAvailableActions() {
        List<GameAction> actions = new ArrayList<>();

        GameAction lookAround = new GameAction();
        lookAround.setId("look-around");
        lookAround.setLabel("Осмотреть окрестности");
        lookAround.setDescription("Осмотрите окрестности, чтобы найти точки интереса");
        lookAround.setEnabled(true);
        actions.add(lookAround);

        GameAction talkToNPC = new GameAction();
        talkToNPC.setId("talk-to-npc");
        talkToNPC.setLabel("Поговорить с NPC");
        talkToNPC.setDescription("Поговорите с одним из доступных NPC");
        talkToNPC.setEnabled(true);
        actions.add(talkToNPC);

        GameAction move = new GameAction();
        move.setId("move");
        move.setLabel("Переместиться");
        move.setDescription("Переместитесь в другую локацию");
        move.setEnabled(true);
        actions.add(move);

        GameAction rest = new GameAction();
        rest.setId("rest");
        rest.setLabel("Отдохнуть");
        rest.setDescription("Отдохните, чтобы восстановить здоровье и энергию");
        rest.setEnabled(true);
        actions.add(rest);

        GameAction inventory = new GameAction();
        inventory.setId("inventory");
        inventory.setLabel("Открыть инвентарь");
        inventory.setDescription("Откройте инвентарь для управления предметами");
        inventory.setEnabled(true);
        actions.add(inventory);

        return actions;
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

    private GameNPC mapNPCToDto(NPCEntity entity) {
        GameNPC dto = new GameNPC();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setType(mapNPCType(entity.getType()));
        dto.setFaction(entity.getFaction());
        dto.setGreeting(entity.getGreeting());
        dto.setAvailableQuests(parseAvailableQuests(entity.getAvailableQuests()));
        return dto;
    }

    private GameQuest mapQuestToDto(QuestEntity entity) {
        GameQuest dto = new GameQuest();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setType(mapQuestType(entity.getType()));
        dto.setLevel(entity.getLevel());
        dto.setGiverNpcId(entity.getGiverNpcId());
        dto.setRewards(buildQuestRewards(entity));
        return dto;
    }

    private GameQuestRewards buildQuestRewards(QuestEntity entity) {
        GameQuestRewards rewards = new GameQuestRewards();
        rewards.setExperience(entity.getRewardExperience());
        rewards.setMoney(entity.getRewardMoney());
        rewards.setItems(parseRewardItems(entity.getRewardItems()));
        
        if (entity.getRewardReputationFaction() != null) {
            GameQuestRewards.ReputationChange rep = new GameQuestRewards.ReputationChange();
            rep.setFaction(entity.getRewardReputationFaction());
            rep.setAmount(entity.getRewardReputationAmount());
            rewards.setReputation(rep);
        }
        
        return rewards;
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

    private GameNPC.TypeEnum mapNPCType(NPCEntity.NPCType type) {
        if (type == null) return null;
        return switch (type) {
            case TRADER -> GameNPC.TypeEnum.TRADER;
            case QUEST_GIVER -> GameNPC.TypeEnum.QUEST_GIVER;
            case CITIZEN -> GameNPC.TypeEnum.CITIZEN;
            case ENEMY -> GameNPC.TypeEnum.ENEMY;
        };
    }

    private GameQuest.TypeEnum mapQuestType(QuestEntity.QuestType type) {
        if (type == null) return null;
        return switch (type) {
            case MAIN -> GameQuest.TypeEnum.MAIN;
            case SIDE -> GameQuest.TypeEnum.SIDE;
            case CONTRACT -> GameQuest.TypeEnum.CONTRACT;
        };
    }

    private List<String> parseConnectedLocations(String connectedLocations) {
        if (connectedLocations == null || connectedLocations.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(connectedLocations.replaceAll("[\\[\\]\"]", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> parseAvailableQuests(String availableQuests) {
        if (availableQuests == null || availableQuests.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(availableQuests.replaceAll("[\\[\\]\"]", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> parseRewardItems(String rewardItems) {
        if (rewardItems == null || rewardItems.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(rewardItems.replaceAll("[\\[\\]\"]", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}

