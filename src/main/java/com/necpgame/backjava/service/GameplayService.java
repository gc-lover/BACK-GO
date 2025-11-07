package com.necpgame.backjava.service;

import com.necpgame.backjava.model.CombatEndResult;
import com.necpgame.backjava.model.CombatSession;
import com.necpgame.backjava.model.CreateCombatSessionRequest;
import com.necpgame.backjava.model.DamageRequest;
import com.necpgame.backjava.model.DamageResult;
import com.necpgame.backjava.model.EndCombatSessionRequest;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetCombatEvents200Response;
import com.necpgame.backjava.model.NextTurn200Response;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import java.util.UUID;
import com.necpgame.backjava.model.UpdateParticipantStatusRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for GameplayService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface GameplayService {

    /**
     * POST /gameplay/combat/sessions/{session_id}/damage : Нанести урон
     *
     * @param sessionId  (required)
     * @param damageRequest  (required)
     * @return DamageResult
     */
    DamageResult applyDamage(@NonNull UUID sessionId, DamageRequest damageRequest);

    /**
     * POST /gameplay/combat/sessions : Создать боевую сессию
     *
     * @param createCombatSessionRequest  (required)
     * @return CombatSession
     */
    CombatSession createCombatSession(CreateCombatSessionRequest createCombatSessionRequest);

    /**
     * POST /gameplay/combat/sessions/{session_id}/end : Завершить бой
     *
     * @param sessionId  (required)
     * @param endCombatSessionRequest  (required)
     * @return CombatEndResult
     */
    CombatEndResult endCombatSession(@NonNull UUID sessionId, EndCombatSessionRequest endCombatSessionRequest);

    /**
     * GET /gameplay/combat/sessions/{session_id}/events : Получить лог событий боя
     *
     * @param sessionId  (required)
     * @param sinceEventId Получить события после указанного ID (optional)
     * @return GetCombatEvents200Response
     */
    GetCombatEvents200Response getCombatEvents(@NonNull UUID sessionId, Integer sinceEventId);

    /**
     * GET /gameplay/combat/sessions/{session_id} : Получить состояние боевой сессии
     *
     * @param sessionId  (required)
     * @return CombatSession
     */
    CombatSession getCombatSession(@NonNull UUID sessionId);

    /**
     * POST /gameplay/combat/sessions/{session_id}/turn/next : Следующий ход
     * Для turn-based combat
     *
     * @param sessionId  (required)
     * @return NextTurn200Response
     */
    NextTurn200Response nextTurn(@NonNull UUID sessionId);

    /**
     * PUT /gameplay/combat/sessions/{session_id}/participants/{participant_id}/status : Обновить статус участника
     *
     * @param sessionId  (required)
     * @param participantId  (required)
     * @param updateParticipantStatusRequest  (required)
     * @return Void
     */
    Void updateParticipantStatus(@NonNull UUID sessionId, @NonNull String participantId, UpdateParticipantStatusRequest updateParticipantStatusRequest);
}

