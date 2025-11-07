package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.NarrativeApi;
import com.necpgame.backjava.model.CompleteQuestRequest;
import com.necpgame.backjava.model.DialogueChoiceRequest;
import com.necpgame.backjava.model.DialogueChoiceResult;
import com.necpgame.backjava.model.DialogueNode;
import com.necpgame.backjava.model.GetActiveQuests200Response;
import com.necpgame.backjava.model.QuestCompletionResult;
import com.necpgame.backjava.model.QuestInstance;
import com.necpgame.backjava.model.SkillCheckRequest;
import com.necpgame.backjava.model.SkillCheckResult;
import com.necpgame.backjava.model.StartQuestRequest;
import com.necpgame.backjava.service.NarrativeService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NarrativeController implements NarrativeApi {

    private final NarrativeService narrativeService;

    @Override
    public ResponseEntity<Void> abandonQuest(UUID instanceId) {
        narrativeService.abandonQuest(instanceId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<DialogueChoiceResult> chooseDialogueOption(UUID instanceId, DialogueChoiceRequest dialogueChoiceRequest) {
        DialogueChoiceResult result = narrativeService.chooseDialogueOption(instanceId, dialogueChoiceRequest);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<QuestCompletionResult> completeQuest(UUID instanceId, CompleteQuestRequest completeQuestRequest) {
        QuestCompletionResult result = narrativeService.completeQuest(instanceId, completeQuestRequest);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<GetActiveQuests200Response> getActiveQuests(UUID characterId) {
        GetActiveQuests200Response response = narrativeService.getActiveQuests(characterId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DialogueNode> getCurrentDialogue(UUID instanceId) {
        DialogueNode node = narrativeService.getCurrentDialogue(instanceId);
        return ResponseEntity.ok(node);
    }

    @Override
    public ResponseEntity<QuestInstance> getQuestInstance(UUID instanceId) {
        QuestInstance questInstance = narrativeService.getQuestInstance(instanceId);
        return ResponseEntity.ok(questInstance);
    }

    @Override
    public ResponseEntity<SkillCheckResult> performSkillCheck(UUID instanceId, SkillCheckRequest skillCheckRequest) {
        SkillCheckResult result = narrativeService.performSkillCheck(instanceId, skillCheckRequest);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<QuestInstance> startQuest(StartQuestRequest startQuestRequest) {
        QuestInstance questInstance = narrativeService.startQuest(startQuestRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(questInstance);
    }
}


