package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterHumanityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository для управления человечностью персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
 */
@Repository
public interface CharacterHumanityRepository extends JpaRepository<CharacterHumanityEntity, UUID> {
    
    /**
     * Найти человечность по ID персонажа.
     */
    @Query("SELECT ch FROM CharacterHumanityEntity ch WHERE ch.character.id = :characterId")
    Optional<CharacterHumanityEntity> findByCharacterId(UUID characterId);
    
    /**
     * Проверить существование записи человечности для персонажа.
     */
    @Query("SELECT COUNT(ch) > 0 FROM CharacterHumanityEntity ch WHERE ch.character.id = :characterId")
    boolean existsByCharacterId(UUID characterId);
}

