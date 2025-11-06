package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * NPCsService - сервис для работы с NPC и диалогами.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/npcs/npcs.yaml
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
public interface NPCsService {

    /**
     * Получить список всех NPC.
     */
    GetNPCs200Response getNPCs(UUID characterId, String type);

    /**
     * Получить NPC в локации.
     */
    GetNPCs200Response getNPCsByLocation(String locationId, UUID characterId);

    /**
     * Получить детали NPC.
     */
    NPC getNPCDetails(String npcId, UUID characterId);

    /**
     * Получить диалог с NPC.
     */
    NPCDialogue getNPCDialogue(String npcId, UUID characterId);

    /**
     * Взаимодействовать с NPC.
     */
    InteractWithNPC200Response interactWithNPC(String npcId, InteractWithNPCRequest request);

    /**
     * Ответить в диалоге с NPC.
     */
    NPCDialogue respondToDialogue(String npcId, String dialogueId, RespondToDialogueRequest request);
}

