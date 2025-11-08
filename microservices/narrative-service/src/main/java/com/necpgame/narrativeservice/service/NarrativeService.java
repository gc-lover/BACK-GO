package com.necpgame.narrativeservice.service;

import com.necpgame.narrativeservice.model.GetClassQuests200Response;
import com.necpgame.narrativeservice.model.GetMainStoryQuests200Response;
import com.necpgame.narrativeservice.model.GetOriginStories200Response;
import com.necpgame.narrativeservice.model.GetRecommendedStarterContent200Response;
import com.necpgame.narrativeservice.model.GetStarterProgression200Response;
import org.springframework.lang.Nullable;
import com.necpgame.narrativeservice.model.OriginStoryDetailed;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for NarrativeService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface NarrativeService {

    /**
     * GET /narrative/starter-content/class-quests : Получить классовые стартовые квесты
     *
     * @param classId  (optional)
     * @return GetClassQuests200Response
     */
    GetClassQuests200Response getClassQuests(String classId);

    /**
     * GET /narrative/starter-content/main-story : Получить квесты основного сюжета
     *
     * @param period  (optional)
     * @param chapter  (optional)
     * @param page Номер страницы (начинается с 1) (optional, default to 1)
     * @param pageSize Количество элементов на странице (optional, default to 20)
     * @return GetMainStoryQuests200Response
     */
    GetMainStoryQuests200Response getMainStoryQuests(String period, Integer chapter, Integer page, Integer pageSize);

    /**
     * GET /narrative/starter-content/origin-stories : Получить доступные origin stories
     *
     * @return GetOriginStories200Response
     */
    GetOriginStories200Response getOriginStories();

    /**
     * GET /narrative/starter-content/origin/{origin_id} : Получить детали origin story
     *
     * @param originId  (required)
     * @return OriginStoryDetailed
     */
    OriginStoryDetailed getOriginStory(String originId);

    /**
     * GET /narrative/starter-content/character/{character_id}/recommended : Получить рекомендованный стартовый контент
     * На основе класса, фракции и origin
     *
     * @param characterId  (required)
     * @return GetRecommendedStarterContent200Response
     */
    GetRecommendedStarterContent200Response getRecommendedStarterContent(UUID characterId);

    /**
     * GET /narrative/starter-content/progression : Получить прогрессию стартового контента
     * Правильный порядок квестов для новых игроков
     *
     * @return GetStarterProgression200Response
     */
    GetStarterProgression200Response getStarterProgression();
}

