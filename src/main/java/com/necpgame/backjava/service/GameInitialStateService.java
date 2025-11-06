package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * GameInitialStateService - сервис для получения начального состояния игры.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/game/initial-state.yaml
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
public interface GameInitialStateService {

    /**
     * Получить начальное состояние игры для персонажа.
     * 
     * @param characterId ID персонажа
     * @return начальное состояние игры
     */
    InitialStateResponse getInitialState(UUID characterId);

    /**
     * Получить шаги туториала для нового игрока.
     * 
     * @param characterId ID персонажа
     * @return шаги туториала
     */
    TutorialStepsResponse getTutorialSteps(UUID characterId);
}

