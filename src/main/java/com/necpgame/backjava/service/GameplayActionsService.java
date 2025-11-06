package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

/**
 * GameplayActionsService - сервис для игровых действий.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/gameplay/actions/actions.yaml
 */
public interface GameplayActionsService {

    /**
     * Осмотреться в локации.
     */
    ExploreLocation200Response exploreLocation(ExploreLocationRequest request);

    /**
     * Отдохнуть.
     */
    RestAction200Response restAction(RestActionRequest request);

    /**
     * Использовать объект в локации.
     */
    UseObject200Response useObject(UseObjectRequest request);

    /**
     * Взломать систему.
     */
    HackSystem200Response hackSystem(HackSystemRequest request);
}

