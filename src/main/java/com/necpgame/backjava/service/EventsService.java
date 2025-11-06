package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * EventsService - сервис для работы со случайными событиями.
 * 
 * Сгенерировано на основе: API-SWAGGER/api/v1/events/random-events.yaml
 */
public interface EventsService {

    /**
     * Получить случайное событие.
     */
    RandomEvent getRandomEvent(UUID characterId, String locationId, String context);

    /**
     * Ответить на событие (выбрать опцию).
     */
    EventResult respondToEvent(String eventId, RespondToEventRequest request);

    /**
     * Получить список активных событий персонажа.
     */
    GetActiveEvents200Response getActiveEvents(UUID characterId);
}

