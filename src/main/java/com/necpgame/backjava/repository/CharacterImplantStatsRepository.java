package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterImplantStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository для управления статистикой имплантов персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 */
@Repository
public interface CharacterImplantStatsRepository extends JpaRepository<CharacterImplantStatsEntity, UUID> {
    
    /**
     * Найти статистику по ID персонажа.
     */
    @Query("SELECT cis FROM CharacterImplantStatsEntity cis WHERE cis.character.id = :characterId")
    Optional<CharacterImplantStatsEntity> findByCharacterId(UUID characterId);
    
    /**
     * Проверить существование статистики для персонажа.
     */
    @Query("SELECT COUNT(cis) > 0 FROM CharacterImplantStatsEntity cis WHERE cis.character.id = :characterId")
    boolean existsByCharacterId(UUID characterId);
}

