package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterInventoryEntity;
import com.necpgame.backjava.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CharacterInventoryRepository - репозиторий для работы с инвентарем персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
@Repository
public interface CharacterInventoryRepository extends JpaRepository<CharacterInventoryEntity, UUID> {

    /**
     * Найти все предметы в инвентаре персонажа.
     */
    @Query("SELECT ci FROM CharacterInventoryEntity ci WHERE ci.characterId = :characterId ORDER BY ci.slotPosition")
    List<CharacterInventoryEntity> findByCharacterIdOrderBySlotPosition(UUID characterId);

    /**
     * Найти предмет в инвентаре персонажа.
     */
    @Query("SELECT ci FROM CharacterInventoryEntity ci WHERE ci.characterId = :characterId AND ci.itemId = :itemId")
    Optional<CharacterInventoryEntity> findByCharacterIdAndItemId(UUID characterId, String itemId);

    /**
     * Проверить есть ли предмет в инвентаре.
     */
    @Query("SELECT COUNT(ci) > 0 FROM CharacterInventoryEntity ci WHERE ci.characterId = :characterId AND ci.itemId = :itemId")
    boolean existsByCharacterIdAndItemId(UUID characterId, String itemId);

    /**
     * Посчитать общий вес инвентаря персонажа.
     */
    @Query("SELECT COALESCE(SUM(ii.weight * ci.quantity), 0) FROM CharacterInventoryEntity ci " +
           "JOIN InventoryItemEntity ii ON ci.itemId = ii.id " +
           "WHERE ci.characterId = :characterId")
    BigDecimal calculateTotalWeight(UUID characterId);

    /**
     * Найти предметы по категории в инвентаре персонажа.
     */
    @Query("SELECT ci FROM CharacterInventoryEntity ci " +
           "JOIN InventoryItemEntity ii ON ci.itemId = ii.id " +
           "WHERE ci.characterId = :characterId AND ii.category = :category")
    List<CharacterInventoryEntity> findByCharacterIdAndCategory(UUID characterId, InventoryItemEntity.ItemCategory category);

    /**
     * Удалить предмет из инвентаря.
     */
    void deleteByCharacterIdAndItemId(UUID characterId, String itemId);
}

