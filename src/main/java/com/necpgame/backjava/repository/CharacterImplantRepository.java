package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterImplantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository для управления установленными имплантами персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 */
@Repository
public interface CharacterImplantRepository extends JpaRepository<CharacterImplantEntity, UUID> {
    
    /**
     * Найти все импланты персонажа.
     */
    @Query("SELECT ci FROM CharacterImplantEntity ci WHERE ci.character.id = :characterId")
    List<CharacterImplantEntity> findByCharacterId(UUID characterId);
    
    /**
     * Найти активные импланты персонажа.
     */
    @Query("SELECT ci FROM CharacterImplantEntity ci WHERE ci.character.id = :characterId AND ci.isActive = true")
    List<CharacterImplantEntity> findActiveByCharacterId(UUID characterId);
    
    /**
     * Подсчитать количество активных имплантов персонажа.
     */
    @Query("SELECT COUNT(ci) FROM CharacterImplantEntity ci WHERE ci.character.id = :characterId AND ci.isActive = true")
    Long countActiveByCharacterId(UUID characterId);
    
    /**
     * Проверить существование установленного импланта у персонажа.
     */
    @Query("SELECT COUNT(ci) > 0 FROM CharacterImplantEntity ci WHERE ci.character.id = :characterId AND ci.implant.id = :implantId")
    boolean existsByCharacterIdAndImplantId(UUID characterId, String implantId);
}

