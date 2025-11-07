package com.necpgame.backjava.service;

import com.necpgame.backjava.model.CompleteQuestRequest;
import com.necpgame.backjava.model.DialogueChoiceRequest;
import com.necpgame.backjava.model.DialogueChoiceResult;
import com.necpgame.backjava.model.DialogueNode;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetActiveQuests200Response;
import com.necpgame.backjava.model.QuestCompletionResult;
import com.necpgame.backjava.model.QuestInstance;
import com.necpgame.backjava.model.SkillCheckRequest;
import com.necpgame.backjava.model.SkillCheckResult;
import com.necpgame.backjava.model.StartQuestRequest;
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
     * POST /narrative/quest-engine/instances/{instance_id}/abandon : Отказаться от квеста
     *
     * @param instanceId  (required)
     * @return Void
     */
    Void abandonQuest(UUID instanceId);

    /**
     * POST /narrative/quest-engine/instances/{instance_id}/dialogue/choose : Выбрать вариант диалога
     *
     * @param instanceId  (required)
     * @param dialogueChoiceRequest  (required)
     * @return DialogueChoiceResult
     */
    DialogueChoiceResult chooseDialogueOption(UUID instanceId, DialogueChoiceRequest dialogueChoiceRequest);

    /**
     * POST /narrative/quest-engine/instances/{instance_id}/complete : Завершить квест
     *
     * @param instanceId  (required)
     * @param completeQuestRequest  (required)
     * @return QuestCompletionResult
     */
    QuestCompletionResult completeQuest(UUID instanceId, CompleteQuestRequest completeQuestRequest);

    /**
     * GET /narrative/quest-engine/character/{character_id}/active : Получить активные квесты персонажа
     *
     * @param characterId  (required)
     * @return GetActiveQuests200Response
     */
    GetActiveQuests200Response getActiveQuests(UUID characterId);

    /**
     * GET /narrative/quest-engine/instances/{instance_id}/dialogue : Получить текущий диалог
     *
     * @param instanceId  (required)
     * @return DialogueNode
     */
    DialogueNode getCurrentDialogue(UUID instanceId);

    /**
     * GET /narrative/quest-engine/instances/{instance_id} : Получить состояние квеста
     *
     * @param instanceId  (required)
     * @return QuestInstance
     */
    QuestInstance getQuestInstance(UUID instanceId);

    /**
     * POST /narrative/quest-engine/instances/{instance_id}/skill-check : Выполнить skill check
     * D&amp;D skill check (бросок d20 + модификатор)
     *
     * @param instanceId  (required)
     * @param skillCheckRequest  (required)
     * @return SkillCheckResult
     */
    SkillCheckResult performSkillCheck(UUID instanceId, SkillCheckRequest skillCheckRequest);

    /**
     * POST /narrative/quest-engine/start : Начать новый квест
     *
     * @param startQuestRequest  (required)
     * @return QuestInstance
     */
    QuestInstance startQuest(StartQuestRequest startQuestRequest);
}

