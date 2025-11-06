package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * GameStartService - сервис для запуска игры.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/game/start.yaml
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
public interface GameStartService {

    /**
     * Начать игру для персонажа.
     * 
     * @param request запрос на начало игры
     * @return ответ с данными о начале игры
     */
    GameStartResponse startGame(GameStartRequest request);

    /**
     * Получить приветственный экран для персонажа.
     * 
     * @param characterId ID персонажа
     * @return приветственный экран
     */
    WelcomeScreenResponse getWelcomeScreen(UUID characterId);

    /**
     * Вернуться в игру при повторном входе.
     * 
     * @param request запрос на возврат в игру
     * @return ответ с данными о возврате в игру
     */
    GameReturnResponse returnToGame(GameReturnRequest request);
}

