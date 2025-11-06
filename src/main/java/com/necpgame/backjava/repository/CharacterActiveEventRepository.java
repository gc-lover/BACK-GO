package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterActiveEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CharacterActiveEventRepository - репозиторий для работы с активными событиями персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/events/random-events.yaml
 */
@Repository
public interface CharacterActiveEventRepository extends JpaRepository<CharacterActiveEventEntity, UUID> {

    /**
     * Найти активные события персонажа.
     */
    @Query("SELECT cae FROM CharacterActiveEventEntity cae WHERE cae.characterId = :characterId AND cae.status = 'ACTIVE' ORDER BY cae.createdAt DESC")
    List<CharacterActiveEventEntity> findActiveByCharacterId(UUID characterId);

    /**
     * Найти конкретное активное событие персонажа.
     */
    @Query("SELECT cae FROM CharacterActiveEventEntity cae WHERE cae.characterId = :characterId AND cae.id = :eventId")
    Optional<CharacterActiveEventEntity> findByCharacterIdAndEventId(UUID characterId, UUID eventId);

    /**
     * Проверить есть ли активное событие.
     */
    @Query("SELECT COUNT(cae) > 0 FROM CharacterActiveEventEntity cae WHERE cae.characterId = :characterId AND cae.eventId = :eventId AND cae.status = 'ACTIVE'")
    boolean hasActiveEvent(UUID characterId, String eventId);
}

