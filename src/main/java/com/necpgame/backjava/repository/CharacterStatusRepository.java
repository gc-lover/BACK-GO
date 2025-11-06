package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * CharacterStatusRepository - репозиторий для работы со статусом персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/characters/status.yaml
 */
@Repository
public interface CharacterStatusRepository extends JpaRepository<CharacterStatusEntity, UUID> {

    /**
     * Найти статус персонажа.
     */
    @Query("SELECT cs FROM CharacterStatusEntity cs WHERE cs.characterId = :characterId")
    Optional<CharacterStatusEntity> findByCharacterId(UUID characterId);

    /**
     * Проверить существование статуса персонажа.
     */
    @Query("SELECT COUNT(cs) > 0 FROM CharacterStatusEntity cs WHERE cs.characterId = :characterId")
    boolean existsByCharacterId(UUID characterId);
}

