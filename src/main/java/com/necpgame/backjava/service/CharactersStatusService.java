package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * CharactersStatusService - сервис для работы со статусом и характеристиками персонажа.
 * 
 * Сгенерировано на основе: API-SWAGGER/api/v1/characters/status.yaml
 */
public interface CharactersStatusService {

    /**
     * Получить статус персонажа.
     */
    CharacterStatus getCharacterStatus(UUID characterId);

    /**
     * Получить характеристики персонажа.
     */
    CharacterStats getCharacterStats(UUID characterId);

    /**
     * Получить навыки персонажа.
     */
    GetCharacterSkills200Response getCharacterSkills(UUID characterId);

    /**
     * Обновить статус персонажа (здоровье, энергия, человечность, опыт).
     */
    CharacterStatus updateCharacterStatus(UUID characterId, UpdateCharacterStatusRequest request);
}

