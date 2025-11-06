package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * CharacterStatsRepository - репозиторий для работы с характеристиками персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/characters/status.yaml
 */
@Repository
public interface CharacterStatsRepository extends JpaRepository<CharacterStatsEntity, UUID> {

    /**
     * Найти характеристики персонажа.
     */
    @Query("SELECT cs FROM CharacterStatsEntity cs WHERE cs.characterId = :characterId")
    Optional<CharacterStatsEntity> findByCharacterId(UUID characterId);

    /**
     * Проверить существование характеристик персонажа.
     */
    @Query("SELECT COUNT(cs) > 0 FROM CharacterStatsEntity cs WHERE cs.characterId = :characterId")
    boolean existsByCharacterId(UUID characterId);
}

