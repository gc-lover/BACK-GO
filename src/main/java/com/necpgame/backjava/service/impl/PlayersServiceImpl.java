package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.entity.CharacterAppearanceEntity;
import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.entity.CharacterLocationEntity;
import com.necpgame.backjava.entity.CharacterSkillEntity;
import com.necpgame.backjava.entity.CharacterSlotEntity;
import com.necpgame.backjava.entity.CharacterSlotEntity.SlotType;
import com.necpgame.backjava.entity.CharacterSlotId;
import com.necpgame.backjava.entity.CharacterStatsEntity;
import com.necpgame.backjava.entity.CharacterStatsSnapshotEntity;
import com.necpgame.backjava.entity.CharacterStatusEntity;
import com.necpgame.backjava.entity.CityEntity;
import com.necpgame.backjava.entity.FactionEntity;
import com.necpgame.backjava.entity.GameSessionEntity;
import com.necpgame.backjava.entity.PlayerEntity;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.mapper.CharacterAppearanceMapperMS;
import com.necpgame.backjava.mapper.PlayerCharacterMapper;
import com.necpgame.backjava.mapper.PlayerProfileMapper;
import com.necpgame.backjava.model.CreateCharacterRequest;
import com.necpgame.backjava.model.DeleteCharacter200Response;
import com.necpgame.backjava.model.GameCharacterAppearance;
import com.necpgame.backjava.model.GetCharacters200Response;
import com.necpgame.backjava.model.PlayerCharacter;
import com.necpgame.backjava.model.PlayerCharacterDetails;
import com.necpgame.backjava.model.PlayerProfile;
import com.necpgame.backjava.model.SwitchCharacter200Response;
import com.necpgame.backjava.model.SwitchCharacterRequest;
import com.necpgame.backjava.repository.AccountRepository;
import com.necpgame.backjava.repository.CharacterClassRepository;
import com.necpgame.backjava.repository.CharacterLocationRepository;
import com.necpgame.backjava.repository.CharacterRepository;
import com.necpgame.backjava.repository.CharacterSkillRepository;
import com.necpgame.backjava.repository.CharacterSlotRepository;
import com.necpgame.backjava.repository.CharacterStatsRepository;
import com.necpgame.backjava.repository.CharacterStatsSnapshotRepository;
import com.necpgame.backjava.repository.CharacterStatusRepository;
import com.necpgame.backjava.repository.CityRepository;
import com.necpgame.backjava.repository.FactionRepository;
import com.necpgame.backjava.repository.GameSessionRepository;
import com.necpgame.backjava.repository.PlayerRepository;
import com.necpgame.backjava.service.PlayersService;
import com.necpgame.backjava.util.SecurityUtil;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayersServiceImpl implements PlayersService {

    private static final int BASE_SLOT_COUNT = 3;
    private static final int TOTAL_SLOT_COUNT = 5;
    private static final int RESTORE_GRACE_DAYS = 30;

    private final PlayerRepository playerRepository;
    private final AccountRepository accountRepository;
    private final CharacterRepository characterRepository;
    private final CharacterSlotRepository characterSlotRepository;
    private final CharacterStatusRepository characterStatusRepository;
    private final CharacterStatsRepository characterStatsRepository;
    private final CharacterSkillRepository characterSkillRepository;
    private final CharacterStatsSnapshotRepository characterStatsSnapshotRepository;
    private final CharacterLocationRepository characterLocationRepository;
    private final CharacterClassRepository characterClassRepository;
    private final CityRepository cityRepository;
    private final FactionRepository factionRepository;
    private final GameSessionRepository gameSessionRepository;
    private final CharacterAppearanceMapperMS characterAppearanceMapper;
    private final PlayerProfileMapper playerProfileMapper;
    private final PlayerCharacterMapper playerCharacterMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public PlayerProfile getPlayerProfile() {
        AccountEntity account = getCurrentAccount();
        PlayerEntity player = loadOrCreatePlayer(account);
        return playerProfileMapper.toProfile(player);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCharacters200Response getCharacters(Boolean includeDeleted) {
        AccountEntity account = getCurrentAccount();
        PlayerEntity player = loadOrCreatePlayer(account);
        List<CharacterSlotEntity> slots = syncSlots(player);
        List<CharacterEntity> characters = characterRepository.findAllByAccountId(account.getId());
        Map<UUID, CharacterStatusEntity> statusMap = loadStatuses(characters);
        boolean showDeleted = Boolean.TRUE.equals(includeDeleted);
        List<PlayerCharacter> items = characters.stream()
            .filter(character -> showDeleted || !Boolean.TRUE.equals(character.getDeleted()))
            .sorted(Comparator.comparing(CharacterEntity::getCreatedAt))
            .map(character -> playerCharacterMapper.toSummary(character, statusMap.get(character.getId()), player))
            .collect(Collectors.toList());
        GetCharacters200Response response = new GetCharacters200Response();
        response.setCharacters(items);
        response.setTotalSlots(slots.size());
        long available = characterSlotRepository.countByIdPlayerIdAndUnlockedTrueAndCharacterIdIsNull(player.getId());
        response.setAvailableSlots(Math.toIntExact(available));
        return response;
    }

    @Override
    public PlayerCharacter createCharacter(CreateCharacterRequest request) {
        AccountEntity account = getCurrentAccount();
        PlayerEntity player = loadOrCreatePlayer(account);
        List<CharacterSlotEntity> slots = syncSlots(player);
        CharacterSlotEntity slot = slots.stream()
            .filter(CharacterSlotEntity::isFree)
            .min(Comparator.comparingInt(it -> it.getId().getSlotNumber()))
            .orElseThrow(() -> new BusinessException(ErrorCode.LIMIT_EXCEEDED, "Нет свободных слотов персонажей"));
        if (characterRepository.existsByNameAndAccountId(request.getName(), account.getId())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Имя персонажа уже занято");
        }
        String classCode = request.getPropertyClass().getValue().toLowerCase();
        characterClassRepository.findByClassCode(classCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Класс персонажа не найден"));
        CityEntity city = cityRepository.findById(request.getCityId())
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Город не найден"));
        FactionEntity faction = resolveFaction(request);
        CharacterAppearanceEntity appearance = characterAppearanceMapper.toEntity(request.getAppearance());
        CharacterEntity character = new CharacterEntity();
        character.setAccount(account);
        character.setPlayer(player);
        character.setName(request.getName());
        character.setClassCode(classCode);
        character.setSubclassCode(request.getSubclass() != null && request.getSubclass().isPresent() ? request.getSubclass().get() : null);
        character.setGender(CharacterEntity.Gender.valueOf(request.getGender().getValue()));
        character.setOriginCode(request.getOrigin().getValue());
        character.setFaction(faction);
        character.setCity(city);
        character.setAppearance(appearance);
        character = characterRepository.save(character);
        slot.assignCharacter(character.getId());
        characterSlotRepository.save(slot);
        CharacterStatusEntity status = ensureStatus(character.getId());
        ensureStats(character.getId());
        ensureLocation(character, city);
        return playerCharacterMapper.toSummary(character, status, player);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerCharacterDetails getCharacter(String characterId) {
        UUID id = parseUuid(characterId, "character_id");
        AccountEntity account = getCurrentAccount();
        CharacterEntity character = characterRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Персонаж не найден"));
        if (!character.getAccount().getId().equals(account.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Персонаж принадлежит другому аккаунту");
        }
        PlayerEntity player = loadOrCreatePlayer(account);
        CharacterStatusEntity status = ensureStatus(id);
        CharacterStatsEntity stats = characterStatsRepository.findByCharacterId(id).orElseGet(() -> ensureStats(id));
        List<CharacterSkillEntity> skills = characterSkillRepository.findByCharacterIdOrderByLevelDesc(id);
        CharacterLocationEntity location = characterLocationRepository.findByCharacterId(id).orElse(null);
        GameCharacterAppearance appearanceDto = characterAppearanceMapper.toDto(character.getAppearance());
        PlayerCharacter summary = playerCharacterMapper.toSummary(character, status, player);
        PlayerCharacterDetails details = new PlayerCharacterDetails();
        details.setCharacterId(summary.getCharacterId());
        details.setPlayerId(summary.getPlayerId());
        details.setName(summary.getName());
        details.setClassId(summary.getClassId());
        details.setLevel(summary.getLevel());
        details.setExperience(summary.getExperience());
        details.setCreatedAt(summary.getCreatedAt());
        details.setLastLogin(summary.getLastLogin());
        details.setIsDeleted(summary.getIsDeleted());
        details.setAttributes(buildAttributes(stats));
        details.setSkills(buildSkills(skills));
        details.setReputation(Collections.emptyMap());
        details.setPosition(buildPosition(location, character));
        details.setAppearance(appearanceDto);
        return details;
    }

    @Override
    public DeleteCharacter200Response deleteCharacter(String characterId) {
        UUID id = parseUuid(characterId, "character_id");
        AccountEntity account = getCurrentAccount();
        CharacterEntity character = characterRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Персонаж не найден"));
        if (!character.getAccount().getId().equals(account.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Персонаж принадлежит другому аккаунту");
        }
        if (Boolean.TRUE.equals(character.getDeleted())) {
            return new DeleteCharacter200Response("Персонаж уже находится в состоянии удаления");
        }
        PlayerEntity player = loadOrCreatePlayer(account);
        CharacterStatusEntity status = ensureStatus(id);
        CharacterStatsEntity stats = characterStatsRepository.findByCharacterId(id).orElse(null);
        Map<String, Object> snapshotPayload = buildSnapshot(character, status, stats);
        persistSnapshot(id, snapshotPayload);
        CharacterSlotEntity slot = characterSlotRepository.findByCharacterId(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Слот персонажа не найден"));
        OffsetDateTime deadline = now().plusDays(RESTORE_GRACE_DAYS);
        slot.releaseSlot(deadline);
        characterSlotRepository.save(slot);
        character.setDeleted(true);
        character.setDeletedAt(now());
        character.setRestoreDeadline(deadline);
        characterRepository.save(character);
        return new DeleteCharacter200Response("Персонаж отправлен в резерв на " + RESTORE_GRACE_DAYS + " дней");
    }

    @Override
    public PlayerCharacter restoreCharacter(String characterId) {
        UUID id = parseUuid(characterId, "character_id");
        AccountEntity account = getCurrentAccount();
        CharacterEntity character = characterRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Персонаж не найден"));
        if (!character.getAccount().getId().equals(account.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Персонаж принадлежит другому аккаунту");
        }
        if (!Boolean.TRUE.equals(character.getDeleted())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Персонаж не требует восстановления");
        }
        if (character.getRestoreDeadline() != null && character.getRestoreDeadline().isBefore(now())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Истёк срок восстановления персонажа");
        }
        PlayerEntity player = loadOrCreatePlayer(account);
        List<CharacterSlotEntity> slots = syncSlots(player);
        CharacterStatsSnapshotEntity snapshot = characterStatsSnapshotRepository.findByCharacterId(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Не найдено резервной копии персонажа"));
        CharacterSlotEntity slot = resolveRestoreSlot(player, slots, snapshot.getSlotNumber());
        slot.assignCharacter(id);
        slot.setReservedUntil(null);
        characterSlotRepository.save(slot);
        applySnapshot(id, snapshot.getPayload());
        character.setDeleted(false);
        character.setDeletedAt(null);
        character.setRestoreDeadline(null);
        characterRepository.save(character);
        characterStatsSnapshotRepository.delete(snapshot);
        CharacterStatusEntity status = ensureStatus(id);
        return playerCharacterMapper.toSummary(character, status, player);
    }

    @Override
    public SwitchCharacter200Response switchCharacter(SwitchCharacterRequest request) {
        UUID id = parseUuid(request.getCharacterId(), "character_id");
        AccountEntity account = getCurrentAccount();
        CharacterEntity character = characterRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Персонаж не найден"));
        if (!character.getAccount().getId().equals(account.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Персонаж принадлежит другому аккаунту");
        }
        if (Boolean.TRUE.equals(character.getDeleted())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Персонаж находится в состоянии удаления");
        }
        PlayerEntity player = loadOrCreatePlayer(account);
        closeActiveSessions(account.getId());
        GameSessionEntity session = new GameSessionEntity();
        session.setAccountId(account.getId());
        session.setCharacterId(id);
        session.setLocationId(resolveSessionLocation(character));
        session.setTutorialEnabled(false);
        session.setSessionStart(now().toLocalDateTime());
        session.setIsActive(true);
        session = gameSessionRepository.save(session);
        player.setActiveCharacterId(id);
        playerRepository.save(player);
        SwitchCharacter200Response response = new SwitchCharacter200Response();
        response.setCharacterId(id.toString());
        response.setSessionId(session.getId().toString());
        return response;
    }

    private AccountEntity getCurrentAccount() {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Аккаунт не найден"));
    }

    private PlayerEntity loadOrCreatePlayer(AccountEntity account) {
        return playerRepository.findWithSlotsByAccountId(account.getId())
            .map(player -> {
                syncSlots(player);
                return player;
            })
            .orElseGet(() -> createPlayer(account));
    }

    private PlayerEntity createPlayer(AccountEntity account) {
        PlayerEntity player = new PlayerEntity();
        player.setAccount(account);
        player.setPremiumCurrency(0L);
        player.setSettings(new HashMap<>());
        player.setSlots(new ArrayList<>());
        player = playerRepository.save(player);
        syncSlots(player);
        return playerRepository.findById(player.getId()).orElse(player);
    }

    private List<CharacterSlotEntity> syncSlots(PlayerEntity player) {
        List<CharacterSlotEntity> slots = characterSlotRepository.findByIdPlayerIdOrderByIdSlotNumber(player.getId());
        if (slots.size() == TOTAL_SLOT_COUNT) {
            return slots;
        }
        List<Integer> present = slots.stream()
            .map(slot -> slot.getId().getSlotNumber())
            .collect(Collectors.toList());
        for (int number = 1; number <= TOTAL_SLOT_COUNT; number++) {
            if (!present.contains(number)) {
                CharacterSlotEntity slot = new CharacterSlotEntity();
                slot.setId(new CharacterSlotId(player.getId(), number));
                slot.setPlayer(player);
                slot.setSlotType(number <= BASE_SLOT_COUNT ? SlotType.BASE : SlotType.PREMIUM);
                slot.setUnlocked(number <= BASE_SLOT_COUNT);
                characterSlotRepository.save(slot);
            }
        }
        return characterSlotRepository.findByIdPlayerIdOrderByIdSlotNumber(player.getId());
    }

    private Map<UUID, CharacterStatusEntity> loadStatuses(Collection<CharacterEntity> characters) {
        if (characters.isEmpty()) {
            return Collections.emptyMap();
        }
        List<UUID> ids = characters.stream()
            .map(CharacterEntity::getId)
            .collect(Collectors.toList());
        return characterStatusRepository.findByCharacterIdIn(ids).stream()
            .collect(Collectors.toMap(CharacterStatusEntity::getCharacterId, it -> it));
    }

    private CharacterStatusEntity ensureStatus(UUID characterId) {
        return characterStatusRepository.findByCharacterId(characterId)
            .orElseGet(() -> {
                CharacterStatusEntity status = new CharacterStatusEntity();
                status.setCharacterId(characterId);
                return characterStatusRepository.save(status);
            });
    }

    private CharacterStatsEntity ensureStats(UUID characterId) {
        return characterStatsRepository.findByCharacterId(characterId)
            .orElseGet(() -> {
                CharacterStatsEntity stats = new CharacterStatsEntity();
                stats.setCharacterId(characterId);
                return characterStatsRepository.save(stats);
            });
    }

    private void ensureLocation(CharacterEntity character, CityEntity city) {
        characterLocationRepository.findByCharacterId(character.getId())
            .orElseGet(() -> {
                CharacterLocationEntity location = new CharacterLocationEntity();
                location.setCharacterId(character.getId());
                location.setCurrentLocationId(city.getId().toString());
                return characterLocationRepository.save(location);
            });
    }

    private Map<String, Object> buildAttributes(CharacterStatsEntity stats) {
        if (stats == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("strength", stats.getStrength());
        attributes.put("reflexes", stats.getReflexes());
        attributes.put("intelligence", stats.getIntelligence());
        attributes.put("technical", stats.getTechnical());
        attributes.put("cool", stats.getCool());
        return attributes;
    }

    private List<Map<String, Object>> buildSkills(List<CharacterSkillEntity> skills) {
        if (skills.isEmpty()) {
            return Collections.emptyList();
        }
        return skills.stream()
            .map(skill -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("skill_id", skill.getSkillId());
                dto.put("level", skill.getLevel());
                dto.put("experience", skill.getExperience());
                return dto;
            })
            .collect(Collectors.toList());
    }

    private Map<String, Object> buildPosition(CharacterLocationEntity location, CharacterEntity character) {
        Map<String, Object> position = new HashMap<>();
        position.put("city_id", character.getCity() != null ? character.getCity().getId().toString() : null);
        if (location != null) {
            position.put("current_location_id", location.getCurrentLocationId());
            position.put("previous_location_id", location.getPreviousLocationId());
        }
        return position;
    }

    private Map<String, Object> buildSnapshot(CharacterEntity character, CharacterStatusEntity status, CharacterStatsEntity stats) {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("level", status.getLevel());
        statusMap.put("experience", status.getExperience());
        statusMap.put("next_level_experience", status.getNextLevelExperience());
        payload.put("status", statusMap);
        if (stats != null) {
            payload.put("attributes", buildAttributes(stats));
        }
        payload.put("appearance", characterAppearanceMapper.toDto(character.getAppearance()));
        payload.put("city_id", character.getCity() != null ? character.getCity().getId().toString() : null);
        return payload;
    }

    private void persistSnapshot(UUID characterId, Map<String, Object> payload) {
        CharacterStatsSnapshotEntity snapshot = characterStatsSnapshotRepository.findByCharacterId(characterId)
            .orElseGet(CharacterStatsSnapshotEntity::new);
        snapshot.setCharacterId(characterId);
        CharacterSlotEntity slot = characterSlotRepository.findByCharacterId(characterId).orElse(null);
        if (slot != null) {
            snapshot.setSlotNumber(slot.getId().getSlotNumber());
        }
        snapshot.setPayload(writeJson(payload));
        characterStatsSnapshotRepository.save(snapshot);
    }

    private CharacterSlotEntity resolveRestoreSlot(PlayerEntity player, List<CharacterSlotEntity> slots, Integer preferredSlot) {
        if (preferredSlot != null) {
            CharacterSlotId id = new CharacterSlotId(player.getId(), preferredSlot);
            CharacterSlotEntity preferred = characterSlotRepository.findById(id).orElse(null);
            if (preferred != null && preferred.isFree()) {
                return preferred;
            }
        }
        return slots.stream()
            .filter(CharacterSlotEntity::isFree)
            .min(Comparator.comparingInt(slot -> slot.getId().getSlotNumber()))
            .orElseThrow(() -> new BusinessException(ErrorCode.LIMIT_EXCEEDED, "Нет свободных слотов для восстановления персонажа"));
    }

    private void applySnapshot(UUID characterId, String payload) {
        if (payload == null || payload.isBlank()) {
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode statusNode = root.path("status");
            CharacterStatusEntity status = ensureStatus(characterId);
            if (!statusNode.isMissingNode()) {
                status.setLevel(statusNode.path("level").asInt(status.getLevel()));
                status.setExperience(statusNode.path("experience").asInt(status.getExperience()));
                status.setNextLevelExperience(statusNode.path("next_level_experience").asInt(status.getNextLevelExperience()));
                characterStatusRepository.save(status);
            }
            JsonNode attributesNode = root.path("attributes");
            if (!attributesNode.isMissingNode()) {
                CharacterStatsEntity stats = ensureStats(characterId);
                stats.setStrength(attributesNode.path("strength").asInt(stats.getStrength()));
                stats.setReflexes(attributesNode.path("reflexes").asInt(stats.getReflexes()));
                stats.setIntelligence(attributesNode.path("intelligence").asInt(stats.getIntelligence()));
                stats.setTechnical(attributesNode.path("technical").asInt(stats.getTechnical()));
                stats.setCool(attributesNode.path("cool").asInt(stats.getCool()));
                characterStatsRepository.save(stats);
            }
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Не удалось восстановить данные персонажа");
        }
    }

    private void closeActiveSessions(UUID accountId) {
        List<GameSessionEntity> sessions = gameSessionRepository.findByAccountIdAndIsActiveTrue(accountId);
        if (sessions.isEmpty()) {
            return;
        }
        OffsetDateTime now = now();
        sessions.forEach(session -> {
            session.setIsActive(false);
            session.setSessionEnd(now.toLocalDateTime());
        });
        gameSessionRepository.saveAll(sessions);
    }

    private String resolveSessionLocation(CharacterEntity character) {
        if (character.getCity() != null) {
            return character.getCity().getId().toString();
        }
        return "unknown";
    }

    private OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    private UUID parseUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Некорректный формат поля " + fieldName);
        }
    }

    private FactionEntity resolveFaction(CreateCharacterRequest request) {
        if (request.getFactionId() == null || !request.getFactionId().isPresent()) {
            return null;
        }
        UUID factionId = request.getFactionId().get();
        return factionRepository.findById(factionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Фракция не найдена"));
    }

    private String writeJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Не удалось сохранить резервные данные персонажа");
        }
    }
}

