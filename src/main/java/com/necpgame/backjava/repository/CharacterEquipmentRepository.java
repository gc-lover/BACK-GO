package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterEquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CharacterEquipmentRepository - репозиторий для работы с экипировкой персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
@Repository
public interface CharacterEquipmentRepository extends JpaRepository<CharacterEquipmentEntity, UUID> {

    /**
     * Найти всю экипировку персонажа.
     */
    @Query("SELECT ce FROM CharacterEquipmentEntity ce WHERE ce.characterId = :characterId")
    List<CharacterEquipmentEntity> findByCharacterId(UUID characterId);

    /**
     * Найти предмет в слоте экипировки.
     */
    @Query("SELECT ce FROM CharacterEquipmentEntity ce WHERE ce.characterId = :characterId AND ce.slotType = :slotType")
    Optional<CharacterEquipmentEntity> findByCharacterIdAndSlotType(UUID characterId, CharacterEquipmentEntity.SlotType slotType);

    /**
     * Проверить занят ли слот.
     */
    @Query("SELECT COUNT(ce) > 0 FROM CharacterEquipmentEntity ce WHERE ce.characterId = :characterId AND ce.slotType = :slotType AND ce.itemId IS NOT NULL")
    boolean isSlotOccupied(UUID characterId, CharacterEquipmentEntity.SlotType slotType);

    /**
     * Проверить экипирован ли предмет.
     */
    @Query("SELECT COUNT(ce) > 0 FROM CharacterEquipmentEntity ce WHERE ce.characterId = :characterId AND ce.itemId = :itemId")
    boolean isItemEquipped(UUID characterId, String itemId);

    /**
     * Найти слот в котором экипирован предмет.
     */
    @Query("SELECT ce FROM CharacterEquipmentEntity ce WHERE ce.characterId = :characterId AND ce.itemId = :itemId")
    Optional<CharacterEquipmentEntity> findByCharacterIdAndItemId(UUID characterId, String itemId);
}

