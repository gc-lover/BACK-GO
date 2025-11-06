package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.NPCDialogueOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository для управления опциями диалогов NPC.
 * 
 * Источник: API-SWAGGER/api/v1/npcs/npcs.yaml
 */
@Repository
public interface NPCDialogueOptionRepository extends JpaRepository<NPCDialogueOptionEntity, String> {
    
    /**
     * Найти все опции диалога.
     */
    @Query("SELECT o FROM NPCDialogueOptionEntity o WHERE o.dialogue.id = :dialogueId")
    List<NPCDialogueOptionEntity> findByDialogueId(String dialogueId);
}

