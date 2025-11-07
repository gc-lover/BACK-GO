package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.CombatSessionEventEntity;
import com.necpgame.backjava.entity.CombatSessionInstanceEntity;
import com.necpgame.backjava.entity.CombatSessionParticipantEntity;
import com.necpgame.backjava.model.CombatEndResult;
import com.necpgame.backjava.model.CombatEvent;
import com.necpgame.backjava.model.CombatRewards;
import com.necpgame.backjava.model.CombatRewardsCurrency;
import com.necpgame.backjava.model.CombatSession;
import com.necpgame.backjava.model.CreateCombatSessionRequest;
import com.necpgame.backjava.model.CreateCombatSessionRequestSettings;
import com.necpgame.backjava.model.DamageRequest;
import com.necpgame.backjava.model.DamageResult;
import com.necpgame.backjava.model.EndCombatSessionRequest;
import com.necpgame.backjava.model.GetCombatEvents200Response;
import com.necpgame.backjava.model.NextTurn200Response;
import com.necpgame.backjava.model.Participant;
import com.necpgame.backjava.model.ParticipantInit;
import com.necpgame.backjava.model.ParticipantInitInitialPosition;
import com.necpgame.backjava.model.ParticipantResult;
import com.necpgame.backjava.model.ParticipantResultStats;
import com.necpgame.backjava.model.ParticipantStats;
import com.necpgame.backjava.model.StatusEffect;
import com.necpgame.backjava.model.UpdateParticipantStatusRequest;
import com.necpgame.backjava.repository.CombatSessionEventRepository;
import com.necpgame.backjava.repository.CombatSessionInstanceRepository;
import com.necpgame.backjava.repository.CombatSessionParticipantRepository;
import com.necpgame.backjava.service.GameplayService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.lang.NonNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameplayServiceImpl implements GameplayService {

    private static final TypeReference<List<StatusEffect>> STATUS_EFFECT_LIST = new TypeReference<>() {};
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final CombatSessionInstanceRepository sessionRepository;
    private final CombatSessionParticipantRepository participantRepository;
    private final CombatSessionEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SuppressWarnings("null")
    public CombatSession createCombatSession(CreateCombatSessionRequest request) {
        if (request == null || request.getParticipants() == null || request.getParticipants().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "participants are required");
        }

        CombatSessionInstanceEntity.CombatType combatType = CombatSessionInstanceEntity.CombatType.valueOf(request.getCombatType().getValue());
        CreateCombatSessionRequestSettings settings = request.getSettings();
        boolean turnBased = settings != null && Boolean.TRUE.equals(settings.getTurnBased());
        Integer timeLimitSeconds = null;
        if (settings != null && settings.getTimeLimitSeconds() != null) {
            timeLimitSeconds = settings.getTimeLimitSeconds().orElse(null);
        }

        CombatSessionInstanceEntity session = CombatSessionInstanceEntity.builder()
            .combatType(combatType)
            .status(turnBased ? CombatSessionInstanceEntity.CombatStatus.STARTING : CombatSessionInstanceEntity.CombatStatus.ACTIVE)
            .turnBased(turnBased)
            .timeLimitSeconds(timeLimitSeconds)
            .locationId(request.getLocationId())
            .instanceId(null)
            .settingsJson(writeJson(buildSettingsMap(turnBased, timeLimitSeconds)))
            .startedAt(OffsetDateTime.now())
            .build();

        CombatSessionInstanceEntity persistedSession = Objects.requireNonNull(sessionRepository.save(session));

        List<CombatSessionParticipantEntity> participants = createParticipants(persistedSession.getId(), request.getParticipants());
        participantRepository.saveAll(participants);

        List<String> turnOrder = buildTurnOrder(turnBased, request.getParticipants());
        persistedSession.setTurnOrder(writeJson(turnOrder));
        persistedSession.setCurrentTurnIndex(turnOrder.isEmpty() ? null : 0);
        persistedSession.setActiveParticipantId(turnOrder.isEmpty() ? null : turnOrder.get(0));
        CombatSessionInstanceEntity finalSession = Objects.requireNonNull(sessionRepository.save(persistedSession));

        logEvent(finalSession.getId(), CombatSessionEventEntity.EventType.SESSION_CREATED, null, null, Map.of(
            "combat_type", combatType.name(),
            "turn_based", turnBased
        ));

        return mapToCombatSession(finalSession, participants);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public CombatSession getCombatSession(@NonNull UUID sessionId) {
        CombatSessionInstanceEntity session = loadSession(sessionId);
        List<CombatSessionParticipantEntity> participants = participantRepository.findBySessionIdOrderByOrderIndex(sessionId);
        return mapToCombatSession(session, participants);
    }

    @Override
    @SuppressWarnings("null")
    public DamageResult applyDamage(@NonNull UUID sessionId, DamageRequest damageRequest) {
        loadSession(sessionId);

        String attackerId = Objects.requireNonNull(damageRequest.getAttackerId(), "attacker_id");
        String targetId = Objects.requireNonNull(damageRequest.getTargetId(), "target_id");

        CombatSessionParticipantEntity target = loadParticipant(sessionId, targetId);

        Optional<CombatSessionParticipantEntity> attackerOpt = participantRepository.findBySessionIdAndReferenceId(sessionId, attackerId);

        int damageAmount = Optional.ofNullable(damageRequest.getDamageAmount()).orElse(0);
        int hpBefore = Optional.ofNullable(target.getHp()).orElse(0);
        int finalHp = Math.max(0, hpBefore - Math.max(0, damageAmount));
        target.setHp(finalHp);
        if (finalHp == 0) {
            target.setStatus(CombatSessionParticipantEntity.ParticipantStatus.DEAD);
        }
        target.setDamageTaken(Optional.ofNullable(target.getDamageTaken()).orElse(0) + damageAmount);
        participantRepository.save(target);

        attackerOpt.ifPresent(attacker -> {
            attacker.setDamageDealt(Optional.ofNullable(attacker.getDamageDealt()).orElse(0) + damageAmount);
            if (finalHp == 0) {
                attacker.setKills(Optional.ofNullable(attacker.getKills()).orElse(0) + 1);
            }
            participantRepository.save(attacker);
        });

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("damage", damageAmount);
        payload.put("is_critical", Boolean.TRUE.equals(damageRequest.getIsCritical()));
        DamageRequest.DamageTypeEnum damageType = damageRequest.getDamageType();
        payload.put("damage_type", damageType != null ? damageType.getValue() : null);
        payload.put("weapon_id", damageRequest.getWeaponId());
        payload.put("ability_id", damageRequest.getAbilityId());
        logEvent(sessionId, CombatSessionEventEntity.EventType.DAMAGE, attackerId, targetId, payload);

        DamageResult damageResult = new DamageResult()
            .damageDealt(Math.max(0, damageAmount))
            .damageBlocked(0)
            .isCritical(Boolean.TRUE.equals(damageRequest.getIsCritical()))
            .targetHpBefore(hpBefore)
            .targetHpAfter(finalHp)
            .targetKilled(finalHp == 0)
            .statusEffectsApplied(Collections.emptyList());

        return damageResult;
    }

    @Override
    @SuppressWarnings("null")
    public CombatEndResult endCombatSession(@NonNull UUID sessionId, EndCombatSessionRequest request) {
        CombatSessionInstanceEntity session = loadSession(sessionId);

        if (session.getStatus() == CombatSessionInstanceEntity.CombatStatus.ENDED) {
            return buildCombatEndResult(session, participantRepository.findBySessionIdOrderByOrderIndex(sessionId));
        }

        session.setStatus(CombatSessionInstanceEntity.CombatStatus.ENDED);
        session.setEndedAt(OffsetDateTime.now());
        session.setWinnerTeam(determineWinnerTeam(sessionId));
        sessionRepository.save(session);

        EndCombatSessionRequest.OutcomeEnum outcome = request != null ? request.getOutcome() : null;
        logEvent(sessionId, CombatSessionEventEntity.EventType.SESSION_ENDED, null, null, Map.of(
            "outcome", outcome != null ? outcome.getValue() : null,
            "winner_team", session.getWinnerTeam()
        ));

        List<CombatSessionParticipantEntity> participants = participantRepository.findBySessionIdOrderByOrderIndex(sessionId);
        return buildCombatEndResult(session, participants);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public GetCombatEvents200Response getCombatEvents(@NonNull UUID sessionId, Integer sinceEventId) {
        loadSession(sessionId);

        List<CombatSessionEventEntity> events = sinceEventId == null
            ? eventRepository.findBySessionIdOrderById(sessionId)
            : eventRepository.findBySessionIdAndIdGreaterThanOrderById(sessionId, sinceEventId.longValue());

        List<CombatEvent> result = new ArrayList<>();
        for (CombatSessionEventEntity entity : events) {
            CombatEvent event = new CombatEvent()
                .eventId(entity.getId().intValue())
                .timestamp(entity.getOccurredAt())
                .eventType(CombatEvent.EventTypeEnum.fromValue(entity.getEventType().name()))
                .actorId(entity.getActorId())
                .targetId(entity.getTargetId())
                .data(readPayload(entity.getPayload()));
            result.add(event);
        }

        return new GetCombatEvents200Response().events(result);
    }

    @Override
    @SuppressWarnings("null")
    public NextTurn200Response nextTurn(@NonNull UUID sessionId) {
        CombatSessionInstanceEntity session = loadSession(sessionId);

        if (!session.isTurnBased()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "session is not turn-based");
        }

        List<String> turnOrder = readTurnOrder(session.getTurnOrder());
        if (turnOrder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "turn order is not defined");
        }

        int currentIndex = session.getCurrentTurnIndex() == null ? 0 : session.getCurrentTurnIndex();
        int nextIndex = currentIndex + 1;
        if (nextIndex >= turnOrder.size()) {
            nextIndex = 0;
        }
        session.setCurrentTurnIndex(nextIndex);
        session.setActiveParticipantId(turnOrder.get(nextIndex));
        if (session.getStatus() == CombatSessionInstanceEntity.CombatStatus.STARTING) {
            session.setStatus(CombatSessionInstanceEntity.CombatStatus.ACTIVE);
        }
        sessionRepository.save(session);

        CombatSessionParticipantEntity active = session.getActiveParticipantId() == null
            ? null
            : participantRepository.findBySessionIdAndReferenceId(sessionId, session.getActiveParticipantId()).orElse(null);

        logEvent(sessionId, CombatSessionEventEntity.EventType.TURN_START, session.getActiveParticipantId(), null, Map.of(
            "turn_index", nextIndex
        ));

        NextTurn200Response response = new NextTurn200Response()
            .currentTurn(nextIndex)
            .activeParticipant(active == null ? null : mapToParticipant(active));
        return response;
    }

    @Override
    @SuppressWarnings("null")
    public Void updateParticipantStatus(@NonNull UUID sessionId, @NonNull String participantId, UpdateParticipantStatusRequest request) {
        CombatSessionParticipantEntity participant = loadParticipant(sessionId, participantId);

        if (request != null && request.getHp() != null) {
            Integer requestedHp = request.getHp();
            int hp = Math.max(0, requestedHp);
            participant.setHp(hp);
            if (hp == 0) {
                participant.setStatus(CombatSessionParticipantEntity.ParticipantStatus.DEAD);
            } else if (participant.getStatus() == CombatSessionParticipantEntity.ParticipantStatus.DEAD) {
                participant.setStatus(CombatSessionParticipantEntity.ParticipantStatus.ALIVE);
            }
        }

        if (request != null && request.getStatusEffects() != null) {
            participant.setStatusEffectsJson(writeJson(request.getStatusEffects()));
        }

        participantRepository.save(participant);

        logEvent(sessionId, CombatSessionEventEntity.EventType.STATUS_EFFECT, participantId, null, Map.of(
            "hp", participant.getHp()
        ));

        return null;
    }

    private List<CombatSessionParticipantEntity> createParticipants(UUID sessionId, List<ParticipantInit> participantInitList) {
        List<CombatSessionParticipantEntity> result = new ArrayList<>();
        int index = 0;
        for (ParticipantInit init : participantInitList) {
            String participantId = Objects.requireNonNull(init.getId(), "participant_id");
            ParticipantInit.TypeEnum typeEnum = Objects.requireNonNull(init.getType(), "participant_type");
            CombatSessionParticipantEntity entity = CombatSessionParticipantEntity.builder()
                .sessionId(sessionId)
                .participantType(CombatSessionParticipantEntity.ParticipantType.valueOf(typeEnum.getValue()))
                .referenceId(participantId)
                .team(init.getTeam())
                .characterName(participantId)
                .hp(100)
                .maxHp(100)
                .status(CombatSessionParticipantEntity.ParticipantStatus.ALIVE)
                .statusEffectsJson(writeJson(Collections.emptyList()))
                .positionJson(init.getInitialPosition() == null ? null : writeJson(toPositionMap(init.getInitialPosition())))
                .damageDealt(0)
                .damageTaken(0)
                .kills(0)
                .deaths(0)
                .headshots(0)
                .abilitiesUsed(0)
                .orderIndex(index++)
                .build();
            result.add(entity);
        }
        return result;
    }

    private List<String> buildTurnOrder(boolean turnBased, List<ParticipantInit> participants) {
        if (!turnBased) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        for (ParticipantInit init : participants) {
            ids.add(init.getId());
        }
        ids.sort(Comparator.naturalOrder());
        return ids;
    }

    private CombatSession mapToCombatSession(CombatSessionInstanceEntity session, List<CombatSessionParticipantEntity> participantEntities) {
        CombatSession combatSession = new CombatSession()
            .id(session.getId())
            .combatType(CombatSession.CombatTypeEnum.fromValue(session.getCombatType().name()))
            .status(CombatSession.StatusEnum.fromValue(session.getStatus().name()))
            .participants(mapParticipants(participantEntities))
            .currentTurn(session.getCurrentTurnIndex())
            .activeParticipantId(session.getActiveParticipantId())
            .startedAt(session.getStartedAt())
            .endedAt(session.getEndedAt())
            .durationSeconds(calculateDurationSeconds(session))
            .winnerTeam(session.getWinnerTeam());
        return combatSession;
    }

    private List<Participant> mapParticipants(List<CombatSessionParticipantEntity> participants) {
        List<Participant> result = new ArrayList<>();
        for (CombatSessionParticipantEntity entity : participants) {
            result.add(mapToParticipant(entity));
        }
        return result;
    }

    private Participant mapToParticipant(CombatSessionParticipantEntity entity) {
        Participant participant = new Participant()
            .id(entity.getReferenceId())
            .type(Participant.TypeEnum.fromValue(entity.getParticipantType().name()))
            .team(entity.getTeam())
            .characterName(entity.getCharacterName())
            .hp(entity.getHp())
            .maxHp(entity.getMaxHp())
            .status(Participant.StatusEnum.fromValue(entity.getStatus().name()))
            .statusEffects(readStatusEffects(entity.getStatusEffectsJson()))
            .position(readPosition(entity.getPositionJson()))
            .stats(new ParticipantStats()
                .damageDealt(entity.getDamageDealt())
                .damageTaken(entity.getDamageTaken())
                .kills(entity.getKills())
                .deaths(entity.getDeaths()));
        return participant;
    }

    private Integer calculateDurationSeconds(CombatSessionInstanceEntity session) {
        OffsetDateTime started = session.getStartedAt();
        OffsetDateTime ended = session.getEndedAt() != null ? session.getEndedAt() : OffsetDateTime.now();
        if (started == null) {
            return null;
        }
        return Math.toIntExact(Duration.between(started, ended).getSeconds());
    }

    private CombatEndResult buildCombatEndResult(CombatSessionInstanceEntity session, List<CombatSessionParticipantEntity> participants) {
        List<ParticipantResult> participantResults = new ArrayList<>();
        for (CombatSessionParticipantEntity participant : participants) {
            participantResults.add(new ParticipantResult()
                .participantId(participant.getReferenceId())
                .type(participant.getParticipantType().name())
                .finalStatus(ParticipantResult.FinalStatusEnum.fromValue(participant.getStatus().name()))
                .stats(new ParticipantResultStats()
                    .damageDealt(participant.getDamageDealt())
                    .damageTaken(participant.getDamageTaken())
                    .kills(participant.getKills())
                    .deaths(participant.getDeaths())
                    .headshots(participant.getHeadshots())
                    .abilitiesUsed(participant.getAbilitiesUsed())));
        }

        CombatRewards rewards = new CombatRewards()
            .experience(session.getWinnerTeam() != null ? 150 : 50)
            .currency(new CombatRewardsCurrency().eddies(session.getWinnerTeam() != null ? 75 : 25))
            .loot(Collections.emptyList());

        CombatEndResult result = new CombatEndResult()
            .sessionId(session.getId())
            .outcome(determineOutcome(session))
            .winnerTeam(session.getWinnerTeam())
            .durationSeconds(calculateDurationSeconds(session))
            .participantResults(participantResults)
            .rewards(rewards);
        return result;
    }

    private CombatEndResult.OutcomeEnum determineOutcome(CombatSessionInstanceEntity session) {
        if (session.getWinnerTeam() == null) {
            return CombatEndResult.OutcomeEnum.DRAW;
        }
        return CombatEndResult.OutcomeEnum.VICTORY;
    }

    private String determineWinnerTeam(UUID sessionId) {
        Map<String, Long> aliveByTeam = new LinkedHashMap<>();
        participantRepository.findBySessionIdOrderByOrderIndex(sessionId).forEach(participant -> {
            if (participant.getTeam() != null && participant.getStatus() != CombatSessionParticipantEntity.ParticipantStatus.DEAD) {
                aliveByTeam.merge(participant.getTeam(), 1L, (left, right) -> left + right);
            }
        });
        if (aliveByTeam.size() == 1) {
            return aliveByTeam.keySet().iterator().next();
        }
        return null;
    }

    private Map<String, Object> buildSettingsMap(boolean turnBased, Integer timeLimitSeconds) {
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("turn_based", turnBased);
        settings.put("time_limit_seconds", timeLimitSeconds);
        return settings;
    }

    private ParticipantInitInitialPosition readPosition(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        Map<String, Object> map = readPayload(json);
        ParticipantInitInitialPosition position = new ParticipantInitInitialPosition();
        if (map.get("x") instanceof Number valueX) {
            position.setX(BigDecimal.valueOf(valueX.doubleValue()));
        }
        if (map.get("y") instanceof Number valueY) {
            position.setY(BigDecimal.valueOf(valueY.doubleValue()));
        }
        if (map.get("z") instanceof Number valueZ) {
            position.setZ(BigDecimal.valueOf(valueZ.doubleValue()));
        }
        return position;
    }

    private Map<String, Object> toPositionMap(ParticipantInitInitialPosition position) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (position.getX() != null) {
            map.put("x", position.getX());
        }
        if (position.getY() != null) {
            map.put("y", position.getY());
        }
        if (position.getZ() != null) {
            map.put("z", position.getZ());
        }
        return map;
    }

    private List<StatusEffect> readStatusEffects(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, STATUS_EFFECT_LIST);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to read status effects: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> readTurnOrder(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, STRING_LIST);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to read turn order: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, Object> readPayload(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to read payload: {}", ex.getMessage());
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("null")
    private void logEvent(UUID sessionId, CombatSessionEventEntity.EventType type, String actorId, String targetId, Map<String, Object> payload) {
        CombatSessionEventEntity entity = CombatSessionEventEntity.builder()
            .sessionId(sessionId)
            .eventType(type)
            .actorId(actorId)
            .targetId(targetId)
            .payload(writeJson(payload))
            .build();
        eventRepository.save(entity);
    }

    @SuppressWarnings("null")
    private @NonNull CombatSessionInstanceEntity loadSession(@NonNull UUID sessionId) {
        return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "combat session not found"));
    }

    @SuppressWarnings("null")
    private @NonNull CombatSessionParticipantEntity loadParticipant(@NonNull UUID sessionId, @NonNull String participantId) {
        String safeParticipantId = Objects.requireNonNull(participantId, "participant_id");
        return participantRepository.findBySessionIdAndReferenceId(sessionId, safeParticipantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "participant not found"));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialise value", ex);
        }
    }
}


