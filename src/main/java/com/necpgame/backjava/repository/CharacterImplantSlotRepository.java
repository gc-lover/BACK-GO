package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterImplantSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для управления слотами имплантов персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 */
@Repository
public interface CharacterImplantSlotRepository extends JpaRepository<CharacterImplantSlotEntity, UUID> {
    
    /**
     * Найти все слоты персонажа.
     */
    @Query("SELECT cis FROM CharacterImplantSlotEntity cis WHERE cis.character.id = :characterId")
    List<CharacterImplantSlotEntity> findByCharacterId(UUID characterId);
    
    /**
     * Найти слот персонажа по типу.
     */
    @Query("SELECT cis FROM CharacterImplantSlotEntity cis WHERE cis.character.id = :characterId AND cis.slotType = :slotType")
    Optional<CharacterImplantSlotEntity> findByCharacterIdAndSlotType(UUID characterId, CharacterImplantSlotEntity.SlotType slotType);
}

