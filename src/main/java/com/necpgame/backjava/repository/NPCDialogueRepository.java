package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.NPCDialogueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для управления диалогами NPC.
 * 
 * Источник: API-SWAGGER/api/v1/npcs/npcs.yaml
 */
@Repository
public interface NPCDialogueRepository extends JpaRepository<NPCDialogueEntity, String> {
    
    /**
     * Найти все диалоги NPC.
     */
    @Query("SELECT d FROM NPCDialogueEntity d WHERE d.npc.id = :npcId")
    List<NPCDialogueEntity> findByNpcId(String npcId);
    
    /**
     * Найти начальный диалог NPC.
     */
    @Query("SELECT d FROM NPCDialogueEntity d WHERE d.npc.id = :npcId AND d.isInitial = true")
    Optional<NPCDialogueEntity> findInitialDialogueByNpcId(String npcId);
}

