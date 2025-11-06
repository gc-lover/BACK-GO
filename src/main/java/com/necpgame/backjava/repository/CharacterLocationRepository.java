package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * CharacterLocationRepository - репозиторий для работы с локациями персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/locations/locations.yaml
 */
@Repository
public interface CharacterLocationRepository extends JpaRepository<CharacterLocationEntity, UUID> {

    /**
     * Найти текущую локацию персонажа.
     */
    @Query("SELECT cl FROM CharacterLocationEntity cl WHERE cl.characterId = :characterId")
    Optional<CharacterLocationEntity> findByCharacterId(UUID characterId);

    /**
     * Проверить существование записи о локации персонажа.
     */
    @Query("SELECT COUNT(cl) > 0 FROM CharacterLocationEntity cl WHERE cl.characterId = :characterId")
    boolean existsByCharacterId(UUID characterId);
}

