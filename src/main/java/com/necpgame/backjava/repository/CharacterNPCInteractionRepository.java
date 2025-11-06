package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterNPCInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для управления взаимодействиями персонажа с NPC.
 * 
 * Источник: API-SWAGGER/api/v1/npcs/npcs.yaml
 */
@Repository
public interface CharacterNPCInteractionRepository extends JpaRepository<CharacterNPCInteractionEntity, UUID> {
    
    /**
     * Найти взаимодействие персонажа с NPC.
     */
    @Query("SELECT i FROM CharacterNPCInteractionEntity i WHERE i.character.id = :characterId AND i.npc.id = :npcId")
    Optional<CharacterNPCInteractionEntity> findByCharacterIdAndNpcId(UUID characterId, String npcId);
    
    /**
     * Найти все взаимодействия персонажа.
     */
    @Query("SELECT i FROM CharacterNPCInteractionEntity i WHERE i.character.id = :characterId")
    List<CharacterNPCInteractionEntity> findByCharacterId(UUID characterId);
}

