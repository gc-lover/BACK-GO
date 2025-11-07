package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.entity.CharacterInventoryEntity;
import com.necpgame.backjava.entity.CharacterInventoryEntity.StorageType;
import com.necpgame.backjava.entity.CharacterLocationEntity;
import com.necpgame.backjava.entity.CharacterStatsEntity;
import com.necpgame.backjava.entity.CharacterStatusEntity;
import com.necpgame.backjava.entity.NotificationEntity;
import com.necpgame.backjava.entity.PartyEntity;
import com.necpgame.backjava.entity.QuestEntity;
import com.necpgame.backjava.entity.QuestProgressEntity;
import com.necpgame.backjava.entity.mvp.MvpContentOverviewEntity;
import com.necpgame.backjava.entity.mvp.MvpContentStatusEntity;
import com.necpgame.backjava.entity.mvp.MvpEndpointEntity;
import com.necpgame.backjava.entity.mvp.MvpStarterFactionEntity;
import com.necpgame.backjava.entity.mvp.MvpStarterItemEntity;
import com.necpgame.backjava.entity.mvp.MvpStarterLocationEntity;
import com.necpgame.backjava.entity.mvp.MvpStarterNpcEntity;
import com.necpgame.backjava.entity.mvp.MvpStarterQuestEntity;
import com.necpgame.backjava.entity.mvp.MvpWorldStateEntity;
import com.necpgame.backjava.mapper.MvpContentMapper;
import com.necpgame.backjava.model.ContentOverview;
import com.necpgame.backjava.model.ContentStatus;
import com.necpgame.backjava.model.GetMVPEndpoints200Response;
import com.necpgame.backjava.model.GetMVPHealth200Response;
import com.necpgame.backjava.model.GetMVPHealth200Response.StatusEnum;
import com.necpgame.backjava.model.GetMVPHealth200ResponseSystems;
import com.necpgame.backjava.model.GetMVPModels200Response;
import com.necpgame.backjava.model.InitialGameData;
import com.necpgame.backjava.model.InitialGameDataNpcsInner;
import com.necpgame.backjava.model.InitialGameDataStarterItemsInner;
import com.necpgame.backjava.model.MainGameUIData;
import com.necpgame.backjava.model.MainGameUIDataCharacter;
import com.necpgame.backjava.model.MainGameUIDataInventory;
import com.necpgame.backjava.model.MainGameUIDataWorldState;
import com.necpgame.backjava.model.TextVersionState;
import com.necpgame.backjava.model.TextVersionStateAvailableActionsInner;
import com.necpgame.backjava.model.TextVersionStateCharacter;
import com.necpgame.backjava.model.TextVersionStateCurrentQuest;
import com.necpgame.backjava.model.TextVersionStateInventorySummary;
import com.necpgame.backjava.model.TextVersionStateNearbyNpcsInner;
import com.necpgame.backjava.repository.CharacterInventoryRepository;
import com.necpgame.backjava.repository.CharacterLocationRepository;
import com.necpgame.backjava.repository.CharacterRepository;
import com.necpgame.backjava.repository.CharacterStatsRepository;
import com.necpgame.backjava.repository.CharacterStatusRepository;
import com.necpgame.backjava.repository.GameLocationRepository;
import com.necpgame.backjava.repository.NotificationRepository;
import com.necpgame.backjava.repository.PartyRepository;
import com.necpgame.backjava.repository.QuestProgressRepository;
import com.necpgame.backjava.repository.QuestRepository;
import com.necpgame.backjava.repository.mvp.MvpContentOverviewRepository;
import com.necpgame.backjava.repository.mvp.MvpContentStatusRepository;
import com.necpgame.backjava.repository.mvp.MvpEndpointRepository;
import com.necpgame.backjava.repository.mvp.MvpModelRepository;
import com.necpgame.backjava.repository.mvp.MvpStarterFactionRepository;
import com.necpgame.backjava.repository.mvp.MvpStarterItemRepository;
import com.necpgame.backjava.repository.mvp.MvpStarterLocationRepository;
import com.necpgame.backjava.repository.mvp.MvpStarterNpcRepository;
import com.necpgame.backjava.repository.mvp.MvpStarterQuestRepository;
import com.necpgame.backjava.repository.mvp.MvpTextActionRepository;
import com.necpgame.backjava.repository.mvp.MvpTextNearbyNpcRepository;
import com.necpgame.backjava.repository.mvp.MvpWorldStateRepository;
import com.necpgame.backjava.service.MvpContentService;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MvpContentServiceImpl implements MvpContentService {

    private static final int MAX_NOTIFICATIONS = 5;

    private final MvpEndpointRepository endpointRepository;
    private final MvpModelRepository modelRepository;
    private final MvpStarterItemRepository starterItemRepository;
    private final MvpStarterQuestRepository starterQuestRepository;
    private final MvpStarterLocationRepository starterLocationRepository;
    private final MvpStarterNpcRepository starterNpcRepository;
    private final MvpStarterFactionRepository starterFactionRepository;
    private final MvpContentOverviewRepository contentOverviewRepository;
    private final MvpContentStatusRepository contentStatusRepository;
    private final MvpTextActionRepository textActionRepository;
    private final MvpTextNearbyNpcRepository textNearbyNpcRepository;
    private final MvpWorldStateRepository worldStateRepository;
    private final CharacterRepository characterRepository;
    private final CharacterStatusRepository characterStatusRepository;
    private final CharacterStatsRepository characterStatsRepository;
    private final CharacterInventoryRepository characterInventoryRepository;
    private final CharacterLocationRepository characterLocationRepository;
    private final QuestProgressRepository questProgressRepository;
    private final QuestRepository questRepository;
    private final NotificationRepository notificationRepository;
    private final PartyRepository partyRepository;
    private final GameLocationRepository gameLocationRepository;
    private final MvpContentMapper mapper;
    private final ObjectMapper objectMapper;

    public MvpContentServiceImpl(
        MvpEndpointRepository endpointRepository,
        MvpModelRepository modelRepository,
        MvpStarterItemRepository starterItemRepository,
        MvpStarterQuestRepository starterQuestRepository,
        MvpStarterLocationRepository starterLocationRepository,
        MvpStarterNpcRepository starterNpcRepository,
        MvpStarterFactionRepository starterFactionRepository,
        MvpContentOverviewRepository contentOverviewRepository,
        MvpContentStatusRepository contentStatusRepository,
        MvpTextActionRepository textActionRepository,
        MvpTextNearbyNpcRepository textNearbyNpcRepository,
        MvpWorldStateRepository worldStateRepository,
        CharacterRepository characterRepository,
        CharacterStatusRepository characterStatusRepository,
        CharacterStatsRepository characterStatsRepository,
        CharacterInventoryRepository characterInventoryRepository,
        CharacterLocationRepository characterLocationRepository,
        QuestProgressRepository questProgressRepository,
        QuestRepository questRepository,
        NotificationRepository notificationRepository,
        PartyRepository partyRepository,
        GameLocationRepository gameLocationRepository,
        MvpContentMapper mapper,
        ObjectMapper objectMapper
    ) {
        this.endpointRepository = endpointRepository;
        this.modelRepository = modelRepository;
        this.starterItemRepository = starterItemRepository;
        this.starterQuestRepository = starterQuestRepository;
        this.starterLocationRepository = starterLocationRepository;
        this.starterNpcRepository = starterNpcRepository;
        this.starterFactionRepository = starterFactionRepository;
        this.contentOverviewRepository = contentOverviewRepository;
        this.contentStatusRepository = contentStatusRepository;
        this.textActionRepository = textActionRepository;
        this.textNearbyNpcRepository = textNearbyNpcRepository;
        this.worldStateRepository = worldStateRepository;
        this.characterRepository = characterRepository;
        this.characterStatusRepository = characterStatusRepository;
        this.characterStatsRepository = characterStatsRepository;
        this.characterInventoryRepository = characterInventoryRepository;
        this.characterLocationRepository = characterLocationRepository;
        this.questProgressRepository = questProgressRepository;
        this.questRepository = questRepository;
        this.notificationRepository = notificationRepository;
        this.partyRepository = partyRepository;
        this.gameLocationRepository = gameLocationRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public GetMVPEndpoints200Response getEndpoints() {
        List<MvpEndpointEntity> endpoints = endpointRepository.findAllByOrderByCategoryAscEndpointAsc();
        List<com.necpgame.backjava.model.EndpointDefinition> dto = endpoints.stream()
            .map(mapper::toEndpointDefinition)
            .toList();
        Map<String, Integer> categories = mapper.toCategoryMap(endpoints).entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Math.toIntExact(entry.getValue())
            ));
        return new GetMVPEndpoints200Response()
            .endpoints(dto)
            .totalCount(dto.size())
            .categories(categories);
    }

    @Override
    public GetMVPModels200Response getModels() {
        return new GetMVPModels200Response()
            .models(modelRepository.findAllByOrderByModelNameAsc().stream()
                .map(mapper::toModelDefinition)
                .toList());
    }

    @Override
    public InitialGameData getInitialData() {
        List<InitialGameDataStarterItemsInner> starterItems = starterItemRepository.findAllByOrderByItemIdAsc()
            .stream()
            .map(this::toStarterItem)
            .toList();

        List<String> starterQuests = starterQuestRepository.findAllByOrderByQuestCodeAsc()
            .stream()
            .map(MvpStarterQuestEntity::getQuestCode)
            .toList();

        List<String> starterLocations = starterLocationRepository.findAllByOrderByLocationNameAsc()
            .stream()
            .map(MvpStarterLocationEntity::getLocationName)
            .toList();

        List<InitialGameDataNpcsInner> npcs = starterNpcRepository.findAllByOrderByNameAsc()
            .stream()
            .map(this::toNpc)
            .toList();

        List<Object> factions = starterFactionRepository.findAllByOrderByNameAsc()
            .stream()
            .map(this::toFactionMap)
            .toList();

        return new InitialGameData()
            .starterItems(starterItems)
            .starterQuests(starterQuests)
            .starterLocations(starterLocations)
            .npcs(npcs)
            .factions(factions);
    }

    @Override
    public ContentOverview getContentOverview(String period) {
        Optional<MvpContentOverviewEntity> overviewOptional = period == null
            ? contentOverviewRepository.findTopByOrderByPeriodAsc()
            : contentOverviewRepository.findByPeriod(period);

        MvpContentOverviewEntity overview = overviewOptional
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Контент за указанный период не найден"));

        return mapper.toContentOverview(overview);
    }

    @Override
    public ContentStatus getContentStatus() {
        MvpContentStatusEntity statusEntity = contentStatusRepository.findTopByOrderByIdAsc()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Статус MVP контента не инициализирован"));
        return mapper.toContentStatus(statusEntity);
    }

    @Override
    public TextVersionState getTextVersionState(@NotNull UUID characterId) {
        CharacterEntity character = fetchActiveCharacter(characterId);

        CharacterStatusEntity status = characterStatusRepository.findByCharacterId(characterId).orElse(null);
        CharacterLocationEntity location = characterLocationRepository.findByCharacterId(characterId).orElse(null);
        String locationId = location == null ? null : location.getCurrentLocationId();
        String locationName = resolveLocationName(locationId);

        TextVersionStateCharacter characterBlock = new TextVersionStateCharacter()
            .name(character.getName())
            .level(character.getLevel())
            .location(locationName);

        if (status != null) {
            characterBlock
                .hp(status.getHealth())
                .hpMax(status.getMaxHealth());
        }

        List<TextVersionStateAvailableActionsInner> actions = textActionRepository.findAllByOrderByActionAsc()
            .stream()
            .map(mapper::toTextVersionAction)
            .toList();

        List<TextVersionStateNearbyNpcsInner> nearbyNpcs = locationId == null
            ? List.of()
            : textNearbyNpcRepository.findByLocationIdOrderByNpcNameAsc(locationId)
                .stream()
                .map(mapper::toTextVersionNearbyNpc)
                .toList();

        TextVersionStateInventorySummary summary = buildInventorySummary(characterId);

        JsonNullable<TextVersionStateCurrentQuest> currentQuest = buildCurrentQuestBlock(characterId);

        return new TextVersionState()
            .character(characterBlock)
            .availableActions(actions)
            .currentQuest(currentQuest)
            .inventorySummary(summary)
            .nearbyNpcs(nearbyNpcs);
    }

    @Override
    public MainGameUIData getMainGameUi(@NotNull UUID characterId) {
        CharacterEntity character = fetchActiveCharacter(characterId);
        CharacterStatusEntity status = characterStatusRepository.findByCharacterId(characterId).orElse(null);
        CharacterStatsEntity stats = characterStatsRepository.findByCharacterId(characterId).orElse(null);
        CharacterLocationEntity location = characterLocationRepository.findByCharacterId(characterId).orElse(null);

        Map<String, Object> attributes = stats == null ? Map.of() : Map.of(
            "strength", stats.getStrength(),
            "reflexes", stats.getReflexes(),
            "intelligence", stats.getIntelligence(),
            "technical", stats.getTechnical(),
            "cool", stats.getCool()
        );

        MainGameUIDataCharacter characterBlock = new MainGameUIDataCharacter()
            .name(character.getName())
            .level(character.getLevel())
            .experience(status == null ? null : status.getExperience())
            .hp(status == null ? null : status.getHealth())
            .attributes(attributes);

        if (location != null) {
            characterBlock.setLocation(resolveLocationName(location.getCurrentLocationId()));
        }

        MainGameUIDataInventory inventoryBlock = buildInventoryBlock(characterId);
        List<Object> activeQuests = buildActiveQuestSummary(characterId);
        List<Object> notifications = buildNotificationSummary(character.getPlayer().getId());
        JsonNullable<Object> partyInfo = buildPartyInfo(characterId);
        MainGameUIDataWorldState worldState = buildWorldState();

        return new MainGameUIData()
            .character(characterBlock)
            .activeQuests(activeQuests)
            .inventory(inventoryBlock)
            .party(partyInfo.orElse(null))
            .notifications(notifications)
            .worldState(worldState);
    }

    @Override
    public GetMVPHealth200Response getHealthStatus() {
        MvpContentStatusEntity statusEntity = contentStatusRepository.findTopByOrderByIdAsc()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Статус MVP контента не инициализирован"));

        List<MvpEndpointEntity> endpoints = endpointRepository.findAll();

        GetMVPHealth200ResponseSystems systems = new GetMVPHealth200ResponseSystems()
            .auth(resolveCategoryStatus(endpoints, "Authentication"))
            .playerManagement(resolveCategoryStatus(endpoints, "Player"))
            .questEngine(resolveBooleanStatus(statusEntity.isQuestEngineReady()))
            .combatSession(resolveBooleanStatus(statusEntity.isCombatReady()))
            .progression(resolveBooleanStatus(statusEntity.isProgressionReady()));

        StatusEnum aggregateStatus = aggregateStatus(
            systems.getAuth(),
            systems.getPlayerManagement(),
            systems.getQuestEngine(),
            systems.getCombatSession(),
            systems.getProgression(),
            statusEntity.isMvpContentReady()
        );

        return new GetMVPHealth200Response()
            .status(aggregateStatus)
            .systems(systems);
    }

    private InitialGameDataStarterItemsInner toStarterItem(MvpStarterItemEntity entity) {
        return new InitialGameDataStarterItemsInner()
            .itemId(entity.getItemId())
            .quantity(entity.getQuantity());
    }

    private InitialGameDataNpcsInner toNpc(MvpStarterNpcEntity entity) {
        String locationName = Optional.ofNullable(entity.getLocation())
            .map(MvpStarterLocationEntity::getLocationName)
            .orElse(null);
        return new InitialGameDataNpcsInner()
            .npcId(entity.getNpcId())
            .name(entity.getName())
            .location(locationName)
            .role(entity.getRole());
    }

    private Map<String, Object> toFactionMap(MvpStarterFactionEntity entity) {
        Map<String, Object> faction = new HashMap<>();
        faction.put("faction_id", entity.getFactionId());
        faction.put("name", entity.getName());
        faction.put("description", entity.getDescription());
        return faction;
    }

    private CharacterEntity fetchActiveCharacter(UUID characterId) {
        return characterRepository.findById(characterId)
            .filter(character -> !character.isDeleted())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Персонаж не найден или удален"));
    }

    private String resolveLocationName(String locationId) {
        if (locationId == null) {
            return null;
        }
        return gameLocationRepository.findById(locationId)
            .map(location -> location.getName())
            .orElse(locationId);
    }

    private TextVersionStateInventorySummary buildInventorySummary(UUID characterId) {
        List<CharacterInventoryEntity> backpackItems =
            characterInventoryRepository.findByCharacterIdAndStorageTypeOrderBySlotPosition(characterId, StorageType.BACKPACK);

        int itemsCount = backpackItems.stream()
            .map(CharacterInventoryEntity::getQuantity)
            .filter(quantity -> quantity != null && quantity > 0)
            .mapToInt(Integer::intValue)
            .sum();

        BigDecimal weight = characterInventoryRepository.calculateTotalWeight(
            characterId,
            List.of(StorageType.BACKPACK, StorageType.EQUIPPED)
        );

        return new TextVersionStateInventorySummary()
            .itemsCount(itemsCount)
            .weight(weight);
    }

    private JsonNullable<TextVersionStateCurrentQuest> buildCurrentQuestBlock(UUID characterId) {
        List<QuestProgressEntity> activeQuests = new ArrayList<>(questProgressRepository.findActiveQuestsByCharacterId(characterId));
        activeQuests.sort(Comparator.comparing(QuestProgressEntity::getStartedAt));

        if (activeQuests.isEmpty()) {
            return JsonNullable.undefined();
        }

        QuestProgressEntity questProgress = activeQuests.getFirst();
        String questName = questRepository.findById(questProgress.getQuestId())
            .map(QuestEntity::getName)
            .orElse(questProgress.getQuestId());

        TextVersionStateCurrentQuest quest = new TextVersionStateCurrentQuest()
            .questName(questName)
            .objectives(List.of("Прогресс: " + questProgress.getProgress() + "%"));
        return JsonNullable.of(quest);
    }

    private MainGameUIDataInventory buildInventoryBlock(UUID characterId) {
        List<CharacterInventoryEntity> backpackItems =
            characterInventoryRepository.findByCharacterIdAndStorageTypeOrderBySlotPosition(characterId, StorageType.BACKPACK);

        List<String> slots = backpackItems.stream()
            .sorted(Comparator.comparing(item -> Optional.ofNullable(item.getSlotPosition()).orElse(Integer.MAX_VALUE)))
            .map(item -> {
                String slotLabel = Optional.ofNullable(item.getSlotPosition())
                    .map(Object::toString)
                    .orElse("-");
                return slotLabel + ":" + item.getItemId() + " x" + item.getQuantity();
            })
            .toList();

        BigDecimal weight = characterInventoryRepository.calculateTotalWeight(
            characterId,
            List.of(StorageType.BACKPACK, StorageType.EQUIPPED)
        );

        return new MainGameUIDataInventory()
            .slots(slots)
            .weight(weight);
    }

    private List<Object> buildActiveQuestSummary(UUID characterId) {
        List<QuestProgressEntity> activeQuests = questProgressRepository.findActiveQuestsByCharacterId(characterId);
        return activeQuests.stream()
            .sorted(Comparator.comparing(QuestProgressEntity::getStartedAt))
            .map(progress -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("quest_id", progress.getQuestId());
                summary.put("status", progress.getStatus().name());
                summary.put("progress", progress.getProgress());
                summary.put("started_at", progress.getStartedAt());
                summary.put("completed_at", progress.getCompletedAt());
                return summary;
            })
            .toList();
    }

    private List<Object> buildNotificationSummary(UUID playerId) {
        return notificationRepository.findByPlayerIdAndIsReadFalse(playerId).stream()
            .sorted(Comparator.comparing(NotificationEntity::getCreatedAt).reversed())
            .limit(MAX_NOTIFICATIONS)
            .map(this::toNotificationMap)
            .toList();
    }

    private Map<String, Object> toNotificationMap(NotificationEntity entity) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", entity.getId());
        notification.put("type", entity.getType());
        notification.put("priority", entity.getPriority());
        notification.put("message", entity.getMessage());
        notification.put("created_at", entity.getCreatedAt());
        return notification;
    }

    private JsonNullable<Object> buildPartyInfo(UUID characterId) {
        String characterIdString = characterId.toString();
        Optional<PartyEntity> partyOptional = partyRepository.findByLeaderCharacterId(characterIdString);

        if (partyOptional.isEmpty()) {
            partyOptional = partyRepository.findByMemberCharacterId(characterIdString);
        }

        if (partyOptional.isEmpty()) {
            return JsonNullable.undefined();
        }

        PartyEntity party = partyOptional.get();
        Map<String, Object> partyMap = new HashMap<>();
        partyMap.put("party_id", party.getPartyId());
        partyMap.put("leader_character_id", party.getLeaderCharacterId());
        partyMap.put("loot_mode", party.getLootMode());
        partyMap.put("max_members", party.getMaxMembers());
        partyMap.put("members", parseMembers(party.getMembersJson()));
        partyMap.put("created_at", party.getCreatedAt());

        return JsonNullable.of(partyMap);
    }

    private List<String> parseMembers(String membersJson) {
        if (membersJson == null || membersJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(membersJson, new TypeReference<List<String>>() { });
        } catch (Exception ex) {
            return List.of(membersJson);
        }
    }

    private MainGameUIDataWorldState buildWorldState() {
        Optional<MvpWorldStateEntity> stateOptional = worldStateRepository.findTopByOrderByCapturedAtDesc();
        if (stateOptional.isEmpty()) {
            return null;
        }
        MvpWorldStateEntity state = stateOptional.get();
        List<String> events = state.getActiveEvents().stream()
            .map(event -> "%s (%s)".formatted(event.getEventName(), event.getSeverity()))
            .toList();
        return new MainGameUIDataWorldState()
            .timeOfDay(state.getTimeOfDay())
            .weather(state.getWeather())
            .activeEvents(events);
    }

    private String resolveCategoryStatus(List<MvpEndpointEntity> endpoints, String categoryPrefix) {
        List<MvpEndpointEntity> categoryEndpoints = endpoints.stream()
            .filter(endpoint -> endpoint.getCategory() != null)
            .filter(endpoint -> endpoint.getCategory().toLowerCase(Locale.ROOT).startsWith(categoryPrefix.toLowerCase(Locale.ROOT)))
            .toList();

        if (categoryEndpoints.isEmpty()) {
            return "UNKNOWN";
        }

        boolean allImplemented = categoryEndpoints.stream().allMatch(MvpEndpointEntity::isImplemented);
        if (allImplemented) {
            return "UP";
        }

        boolean anyImplemented = categoryEndpoints.stream().anyMatch(MvpEndpointEntity::isImplemented);
        return anyImplemented ? "DEGRADED" : "DOWN";
    }

    private String resolveBooleanStatus(boolean ready) {
        return ready ? "UP" : "DOWN";
    }

    private StatusEnum aggregateStatus(
        String auth,
        String playerManagement,
        String questEngine,
        String combatSession,
        String progression,
        boolean overallReady
    ) {
        List<String> statuses = List.of(auth, playerManagement, questEngine, combatSession, progression);
        if (statuses.stream().anyMatch("DOWN"::equalsIgnoreCase)) {
            return StatusEnum.DOWN;
        }
        if (overallReady && statuses.stream().allMatch("UP"::equalsIgnoreCase)) {
            return StatusEnum.HEALTHY;
        }
        return StatusEnum.DEGRADED;
    }
}


