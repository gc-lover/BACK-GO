package com.necpgame.adminservice.service;

import com.necpgame.adminservice.model.CloseSession200Response;
import com.necpgame.adminservice.model.CloseSessionRequest;
import com.necpgame.adminservice.model.CreateSession200Response;
import com.necpgame.adminservice.model.CreateSessionRequest;
import com.necpgame.adminservice.model.Error;
import com.necpgame.adminservice.model.GetActiveSessions200Response;
import org.springframework.lang.Nullable;
import com.necpgame.adminservice.model.ReconnectSession200Response;
import com.necpgame.adminservice.model.ReconnectSessionRequest;
import com.necpgame.adminservice.model.SendHeartbeat200Response;
import com.necpgame.adminservice.model.SendHeartbeatRequest;
import com.necpgame.adminservice.model.SessionState;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for TechnicalService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface TechnicalService {

    /**
     * POST /technical/sessions/close : Закрыть сессию
     * Закрывает игровую сессию при выходе игрока. Сохраняет state персонажа. 
     *
     * @param closeSessionRequest  (required)
     * @return CloseSession200Response
     */
    CloseSession200Response closeSession(CloseSessionRequest closeSessionRequest);

    /**
     * POST /technical/sessions/create : Создать игровую сессию
     * Создает новую игровую сессию при входе игрока. Проверяет на дубликаты (concurrent sessions). 
     *
     * @param createSessionRequest  (required)
     * @return CreateSession200Response
     */
    CreateSession200Response createSession(CreateSessionRequest createSessionRequest);

    /**
     * GET /technical/sessions/active : Получить активные сессии
     * Возвращает список активных сессий (для админов). Может фильтроваться по account_id. 
     *
     * @param accountId  (optional)
     * @return GetActiveSessions200Response
     */
    GetActiveSessions200Response getActiveSessions(String accountId);

    /**
     * GET /technical/sessions/{session_id}/state : Получить состояние сессии
     * Возвращает текущее состояние игровой сессии
     *
     * @param sessionId  (required)
     * @return SessionState
     */
    SessionState getSessionState(String sessionId);

    /**
     * POST /technical/sessions/reconnect : Переподключение
     * Быстрое переподключение к существующей сессии. Восстанавливает state персонажа. 
     *
     * @param reconnectSessionRequest  (required)
     * @return ReconnectSession200Response
     */
    ReconnectSession200Response reconnectSession(ReconnectSessionRequest reconnectSessionRequest);

    /**
     * POST /technical/sessions/heartbeat : Heartbeat (keepalive)
     * Отправляет heartbeat для поддержания сессии активной. Должен вызываться каждые 30 секунд. 
     *
     * @param sendHeartbeatRequest  (required)
     * @return SendHeartbeat200Response
     */
    SendHeartbeat200Response sendHeartbeat(SendHeartbeatRequest sendHeartbeatRequest);
}

